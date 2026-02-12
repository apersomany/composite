package dev.aperso.composite.component

import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import dev.aperso.composite.Composite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.jetbrains.skia.Image
import kotlin.jvm.optionals.getOrNull

val imageCache = LruCache<ResourceLocation, ImageBitmap>(64)

@Composable
fun AssetImage(
    resource: ResourceLocation,
    modifier: Modifier = Modifier,
    contentDescription: String?,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = FilterQuality.None,
) {
    val imageState = produceState(initialValue = imageCache[resource], key1 = resource) {
        if (value == null) {
            value = loadAssetImage(resource)?.also { imageCache.put(resource, it) }
        }
    }
    imageState.value?.let {
        Image(
            bitmap = it,
            modifier = modifier,
            contentDescription = contentDescription,
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality,
        )
    }
}

suspend fun loadAssetImage(location: ResourceLocation): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val resourceManager = Minecraft.getInstance().resourceManager
            val resource = resourceManager.getResource(location).getOrNull() ?: return@withContext null
            resource.open().use { Image.makeFromEncoded(it.readAllBytes()).toComposeImageBitmap() }
        } catch (exception: Exception) {
            Composite.logger.info("Error while loading Minecraft Image", exception)
            null
        }
    }
}