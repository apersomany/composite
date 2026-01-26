package dev.aperso.composite.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import dev.aperso.composite.skia.LocalSkiaSurface
import kotlinx.coroutines.isActive
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import org.lwjgl.opengl.GL30

@Composable
fun Texture(
    texture: ResourceLocation,
    modifier: Modifier = Modifier,
    u: Float = 0f,
    v: Float = 0f,
    w: Float = 1f,
    h: Float = 1f
) {
    val abstractTexture = Minecraft.getInstance().textureManager.getTexture(texture)
    Texture(abstractTexture, modifier, u, v, w, h)
}

@Composable
fun Texture(
    texture: AbstractTexture,
    modifier: Modifier = Modifier,
    u: Float = 0f,
    v: Float = 0f,
    w: Float = 1f,
    h: Float = 1f
) {
    val surface = LocalSkiaSurface.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    
    val minecraft = Minecraft.getInstance()
    val window = minecraft.window
    val guiScale = window.guiScale.toFloat()
    val density = 1f / guiScale

    LaunchedEffect(coordinates, guiScale, texture) {
        coordinates?.let { coordinates ->
            while (isActive) {
                withFrameNanos {
                    surface.record {
                        if (!coordinates.isAttached) return@record
                        val position = coordinates.positionInWindow()
                        val bounds = coordinates.boundsInWindow()
                        val height = window.height
                        GL30.glScissor(
                            bounds.left.toInt(),
                            height - (bounds.top + bounds.height).toInt(),
                            bounds.width.toInt(),
                            bounds.height.toInt()
                        )
                        pose().pushPose()
                        val guiX = position.x * density
                        val guiY = position.y * density
                        val widthGui = bounds.width * density
                        val heightGui = bounds.height * density
                        pose().translate(guiX, guiY, 0f)
                        RenderSystem.setShaderTexture(0, texture.id)
                        RenderSystem.setShader(GameRenderer::getPositionTexShader)
                        val matrix4f = pose().last().pose()
                        val tesselator = Tesselator.getInstance()
                        val buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
                        buffer.addVertex(matrix4f, 0f, heightGui, 0f).setUv(u, v + h)
                        buffer.addVertex(matrix4f, widthGui, heightGui, 0f).setUv(u + w, v + h)
                        buffer.addVertex(matrix4f, widthGui, 0f, 0f).setUv(u + w, v)
                        buffer.addVertex(matrix4f, 0f, 0f, 0f).setUv(u, v)
                        val scissorDisabled = !GL30.glIsEnabled(GL30.GL_SCISSOR_TEST)
                        if (scissorDisabled) RenderSystem.enableScissor(
                            bounds.left.toInt(),
                            height - (bounds.top + bounds.height).toInt(),
                            bounds.width.toInt(),
                            bounds.height.toInt()
                        )
                        BufferUploader.drawWithShader(buffer.buildOrThrow())
                        if (scissorDisabled) RenderSystem.disableScissor()
                        pose().popPose()
                    }
                }
            }
        }
    }
    Spacer(modifier.fillMaxSize().onGloballyPositioned { coordinates = it })
}
