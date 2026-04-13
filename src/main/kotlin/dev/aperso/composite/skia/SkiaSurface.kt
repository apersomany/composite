package dev.aperso.composite.skia

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asComposeCanvas
import com.mojang.blaze3d.opengl.GlStateManager
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import java.util.ArrayDeque
import java.util.Deque
import java.util.concurrent.atomic.AtomicInteger

private const val GL_FRAMEBUFFER = 0x8D40
private const val GL_COLOR_ATTACHMENT0 = 0x8CE0
private const val GL_TEXTURE_2D = 0x0DE1
private const val GL_RGBA8 = 0x8058

private class SkiaBackedTexture : AbstractTexture() {
    fun update(tex: GpuTexture?, view: GpuTextureView?) {
        this.texture = tex
        this.textureView = view
    }

    fun clearRefs() {
        this.texture = null
        this.textureView = null
    }

    override fun close() {
        clearRefs()
    }
}

private class SurfaceResources(
    val fbo: Int,
    val gpuTexture: GpuTexture,
    val gpuTextureView: GpuTextureView,
    val skiaSurface: Surface,
    val backendTarget: BackendRenderTarget,
) {
    fun destroy() {
        SkiaContext.run {
            skiaSurface.close()
            backendTarget.close()
        }
        gpuTextureView.close()
        gpuTexture.close()
        GlStateManager._glDeleteFramebuffers(fbo)
    }
}

private data class DeferredCleanup(
    val resources: SurfaceResources,
    var framesRemaining: Int = 4
)

class SkiaSurface {
    companion object {
        private val counter = AtomicInteger(0)
    }

    private val textureId = Identifier.fromNamespaceAndPath("composite", "skia_surface_${counter.getAndIncrement()}")

    private var currentWidth: Int = 0
    private var currentHeight: Int = 0
    private var active: SurfaceResources? = null

    private val skiaTexture = SkiaBackedTexture()
    private var textureRegistered = false
    private val pendingCleanup = mutableListOf<DeferredCleanup>()

    fun resize(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        if (currentWidth == width && currentHeight == height) return

        skiaTexture.clearRefs()

        active?.let { old ->
            pendingCleanup.add(DeferredCleanup(old))
        }
        active = null

        currentWidth = width
        currentHeight = height

        val device = RenderSystem.getDevice()
        val usage = GpuTexture.USAGE_COPY_DST or
                GpuTexture.USAGE_COPY_SRC or
                GpuTexture.USAGE_TEXTURE_BINDING or
                GpuTexture.USAGE_RENDER_ATTACHMENT

        val gpuTexture = device.createTexture(
            { "Composite Skia Surface" }, usage,
            TextureFormat.RGBA8, width, height, 1, 1
        )
        val gpuTextureView = device.createTextureView(gpuTexture)
        val glId = (gpuTexture as GlTexture).glId()

        val fbo = GlStateManager.glGenFramebuffers()
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glId, 0)
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, 0)

        var skiaSurface: Surface? = null
        var backendTarget: BackendRenderTarget? = null
        SkiaContext.run {
            val bt = BackendRenderTarget.makeGL(width, height, 0, 8, fbo, GL_RGBA8)
            backendTarget = bt
            skiaSurface = Surface.makeFromBackendRenderTarget(
                SkiaContext.directContext, bt,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.sRGB
            ) ?: throw RuntimeException("Failed to create Skia surface")
        }

        active = SurfaceResources(fbo, gpuTexture, gpuTextureView, skiaSurface!!, backendTarget!!)
        skiaTexture.update(gpuTexture, gpuTextureView)
    }

    private val recordedCalls: Deque<GuiGraphicsExtractor.() -> Unit> = ArrayDeque()

    fun record(call: GuiGraphicsExtractor.() -> Unit) {
        recordedCalls.addLast(call)
    }

    private fun processDeferredCleanup() {
        val iterator = pendingCleanup.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.framesRemaining--
            if (entry.framesRemaining <= 0) {
                entry.resources.destroy()
                iterator.remove()
            }
        }
    }

    fun extractRenderState(guiGraphics: GuiGraphicsExtractor, render: (Canvas) -> Unit) {
        processDeferredCleanup()

        val res = active ?: return

        if (!textureRegistered) {
            Minecraft.getInstance().textureManager.register(textureId, skiaTexture)
            textureRegistered = true
        }

        SkiaContext.run {
            SkiaContext.directContext.resetGLAll()
            res.skiaSurface.canvas.clear(0)
            render(res.skiaSurface.canvas.asComposeCanvas())
            res.skiaSurface.flushAndSubmit()
        }

        val guiW = guiGraphics.guiWidth()
        val guiH = guiGraphics.guiHeight()
        guiGraphics.blit(textureId, 0, 0, guiW, guiH, 0f, 1f, 1f, 0f)

        while (true) {
            val call = recordedCalls.poll() ?: break
            call.invoke(guiGraphics)
        }
    }
}

val LocalSkiaSurface = staticCompositionLocalOf<SkiaSurface> { error("No SkiaSurface provided") }
