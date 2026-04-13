package dev.aperso.composite.component

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import net.minecraft.world.item.ItemStack

@Composable
fun Item(item: ItemStack, modifier: Modifier = Modifier, decorations: Boolean = true, tooltip: Boolean = true) {
    val surface = LocalSkiaSurface.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    val minecraft = Minecraft.getInstance()

    LaunchedEffect(Unit) {
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
                    val boundsWidthGui = (bounds.width * density).toInt()
                    val boundsHeightGui = (bounds.height * density).toInt()

                    val itemSize = 16
                    val itemX = guiX + (boundsWidthGui - itemSize) / 2
                    val itemY = guiY + (boundsHeightGui - itemSize) / 2

                    enableScissor(guiX, guiY, guiX + boundsWidthGui, guiY + boundsHeightGui)
                    fakeItem(item, itemX, itemY)
                    if (decorations) itemDecorations(minecraft.font, item, itemX, itemY)
                    disableScissor()

                    if (tooltip && hovered) {
                        val mouseX = (minecraft.mouseHandler.xpos() * density).toInt()
                        val mouseY = (minecraft.mouseHandler.ypos() * density).toInt()
                        setTooltipForNextFrame(minecraft.font, item, mouseX, mouseY)
                    }
                }
            }
        }
    }
    Spacer(modifier.fillMaxSize().onGloballyPositioned { coordinates = it }.hoverable(interactionSource))
}
