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
import dev.aperso.composite.skia.LocalSkiaSurface
import kotlinx.coroutines.isActive
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier

@Composable
fun Texture(
    texture: Identifier,
    modifier: Modifier = Modifier,
    u: Float = 0f,
    v: Float = 0f,
    w: Float = 1f,
    h: Float = 1f
) {
    TextureImpl(texture, modifier, u, v, w, h)
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
    val minecraft = Minecraft.getInstance()
    val dynamicId = remember(texture) {
        val id = Identifier.fromNamespaceAndPath(
            "composite",
            "dynamic_tex_${System.identityHashCode(texture)}"
        )
        minecraft.textureManager.register(id, texture)
        id
    }
    TextureImpl(dynamicId, modifier, u, v, w, h)
}

@Composable
private fun TextureImpl(
    textureLocation: Identifier,
    modifier: Modifier,
    u: Float,
    v: Float,
    w: Float,
    h: Float
) {
    val surface = LocalSkiaSurface.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val minecraft = Minecraft.getInstance()

    LaunchedEffect(textureLocation) {
        while (isActive) {
            withFrameNanos {
                val coords = coordinates ?: return@withFrameNanos
                surface.record {
                    if (!coords.isAttached) return@record

                    val guiScale = minecraft.window.guiScale.toFloat()
                    val density = 1f / guiScale

                    val position = coords.positionInWindow()
                    val bounds = coords.boundsInWindow()

                    val guiX = (position.x * density).toInt()
                    val guiY = (position.y * density).toInt()
                    val widthGui = (bounds.width * density).toInt()
                    val heightGui = (bounds.height * density).toInt()

                    if (widthGui <= 0 || heightGui <= 0) return@record

                    enableScissor(guiX, guiY, guiX + widthGui, guiY + heightGui)
                    blit(
                        textureLocation,
                        guiX, guiY,
                        guiX + widthGui, guiY + heightGui,
                        u, u + w,
                        v, v + h
                    )
                    disableScissor()
                }
            }
        }
    }
    Spacer(modifier.fillMaxSize().onGloballyPositioned { coordinates = it })
}
