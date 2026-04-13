package dev.aperso.composite.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.TextureFormat
import dev.aperso.composite.Composite
import dev.aperso.composite.skia.LocalSkiaSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier
import kotlin.coroutines.resume
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private class AssetTexture(
    label: String,
    nativeImage: NativeImage,
    filterQuality: FilterQuality,
) : AbstractTexture() {
    val width = nativeImage.width
    val height = nativeImage.height

    init {
        val device = RenderSystem.getDevice()
        val usage = GpuTexture.USAGE_COPY_DST or GpuTexture.USAGE_TEXTURE_BINDING
        texture = device.createTexture({ label }, usage, TextureFormat.RGBA8, width, height, 1, 1)
        textureView = device.createTextureView(texture!!)
        sampler = when (filterQuality) {
            FilterQuality.None -> RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST)
            else -> RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR)
        }
        device.createCommandEncoder().writeToTexture(texture!!, nativeImage)
        nativeImage.close()
    }

    override fun close() {
        textureView?.close()
        texture?.close()
        textureView = null
        texture = null
    }
}

private data class AssetTextureData(
    val texture: AssetTexture,
    val textureId: Identifier,
)

private object AssetTextureCache {
    private data class CacheKey(val resource: Identifier, val filterQuality: FilterQuality)
    private class Entry(val data: AssetTextureData, var refCount: Int = 1)

    private val entries = HashMap<CacheKey, Entry>()

    @Synchronized
    fun acquire(resource: Identifier, filterQuality: FilterQuality): AssetTextureData? {
        val entry = entries[CacheKey(resource, filterQuality)] ?: return null
        entry.refCount++
        return entry.data
    }

    @Synchronized
    fun putOrMerge(resource: Identifier, filterQuality: FilterQuality, data: AssetTextureData): AssetTextureData {
        val key = CacheKey(resource, filterQuality)
        val existing = entries[key]
        return if (existing != null) {
            data.texture.close()
            existing.refCount++
            existing.data
        } else {
            entries[key] = Entry(data)
            data
        }
    }

    @Synchronized
    fun release(resource: Identifier, filterQuality: FilterQuality) {
        val key = CacheKey(resource, filterQuality)
        val entry = entries[key] ?: return
        if (--entry.refCount <= 0) {
            entry.data.texture.close()
            entries.remove(key)
        }
    }
}

@Composable
fun AssetImage(
    resource: Identifier,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.None,
) {
    val surface = LocalSkiaSurface.current
    val minecraft = Minecraft.getInstance()

    var textureData by remember { mutableStateOf<AssetTextureData?>(null) }
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    DisposableEffect(resource, filterQuality) {
        onDispose {
            AssetTextureCache.release(resource, filterQuality)
            textureData = null
        }
    }

    LaunchedEffect(resource, filterQuality) {
        textureData = null

        val cached = AssetTextureCache.acquire(resource, filterQuality)
        if (cached != null) {
            textureData = cached
            return@LaunchedEffect
        }

        val nativeImage = withContext(Dispatchers.IO) {
            try {
                val rm = minecraft.resourceManager
                val res = rm.getResource(resource).getOrNull() ?: return@withContext null
                res.open().use { NativeImage.read(it) }
            } catch (e: Exception) {
                Composite.logger.info("Error loading asset image $resource", e)
                null
            }
        } ?: return@LaunchedEffect

        val data = suspendCancellableCoroutine<AssetTextureData?> { cont ->
            cont.invokeOnCancellation { nativeImage.close() }
            minecraft.execute {
                try {
                    val filterStr = if (filterQuality == FilterQuality.None) "nearest" else "linear"
                    val textureId = Identifier.fromNamespaceAndPath(
                        "composite", "asset/$filterStr/${resource.namespace}/${resource.path}"
                    )
                    val tex = AssetTexture(resource.toString(), nativeImage, filterQuality)
                    val finalData = AssetTextureCache.putOrMerge(resource, filterQuality, AssetTextureData(tex, textureId))
                    if (finalData.texture === tex) {
                        minecraft.textureManager.register(textureId, tex)
                    }
                    cont.resume(finalData)
                } catch (e: Exception) {
                    Composite.logger.info("Error creating GPU texture for $resource", e)
                    nativeImage.close()
                    cont.resume(null)
                }
            }
        } ?: return@LaunchedEffect

        textureData = data
    }

    LaunchedEffect(textureData) {
        val data = textureData ?: return@LaunchedEffect
        while (isActive) {
            withFrameNanos {
                val coords = coordinates ?: return@withFrameNanos
                surface.record {
                    if (!coords.isAttached) return@record

                    val guiScale = minecraft.window.guiScale.toFloat()
                    val density = 1f / guiScale

                    val position = coords.positionInWindow()
                    val bounds = coords.boundsInWindow()

                    val guiX = position.x * density
                    val guiY = position.y * density
                    val guiW = bounds.width * density
                    val guiH = bounds.height * density

                    if (guiW <= 0 || guiH <= 0) return@record

                    val w = data.texture.width.toFloat()
                    val h = data.texture.height.toFloat()
                    val scaleX = guiW / w
                    val scaleY = guiH / h

                    val (dstW, dstH) = when (contentScale) {
                        ContentScale.FillBounds -> Pair(guiW, guiH)
                        ContentScale.Crop -> {
                            val s = max(scaleX, scaleY)
                            Pair(w * s, h * s)
                        }
                        ContentScale.FillWidth -> Pair(guiW, h * scaleX)
                        ContentScale.FillHeight -> Pair(w * scaleY, guiH)
                        ContentScale.None -> Pair(w, h)
                        else -> { // Fit
                            val s = min(scaleX, scaleY)
                            Pair(w * s, h * s)
                        }
                    }

                    val alignOffset = alignment.align(
                        IntSize(dstW.roundToInt(), dstH.roundToInt()),
                        IntSize(guiW.roundToInt(), guiH.roundToInt()),
                        LayoutDirection.Ltr
                    )

                    val dstX0 = (guiX + alignOffset.x).roundToInt()
                    val dstY0 = (guiY + alignOffset.y).roundToInt()
                    val dstW0 = dstW.roundToInt()
                    val dstH0 = dstH.roundToInt()

                    val a = (alpha.coerceIn(0f, 1f) * 255).roundToInt()
                    val color = when (val cf = colorFilter) {
                        is BlendModeColorFilter -> {
                            val r = (cf.color.red * 255).roundToInt()
                            val g = (cf.color.green * 255).roundToInt()
                            val b = (cf.color.blue * 255).roundToInt()
                            (a shl 24) or (r shl 16) or (g shl 8) or b
                        }
                        else -> (a shl 24) or 0x00FFFFFF
                    }

                    enableScissor(guiX.toInt(), guiY.toInt(), (guiX + guiW).toInt(), (guiY + guiH).toInt())
                    blit(
                        RenderPipelines.GUI_TEXTURED,
                        data.textureId,
                        dstX0, dstY0,
                        0f, 0f,
                        dstW0, dstH0,
                        data.texture.width, data.texture.height,
                        data.texture.width, data.texture.height,
                        color
                    )
                    disableScissor()
                }
            }
        }
    }

    val baseModifier = if (contentDescription != null) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else modifier
    Spacer(baseModifier.fillMaxSize().onGloballyPositioned { coordinates = it })
}
