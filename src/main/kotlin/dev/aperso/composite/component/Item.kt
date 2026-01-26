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
import net.minecraft.world.item.ItemStack
import org.lwjgl.opengl.GL30
import kotlin.math.min

@Composable
fun Item(item: ItemStack, modifier: Modifier = Modifier, decorations: Boolean = true) {
    val surface = LocalSkiaSurface.current
    var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    
    val minecraft = Minecraft.getInstance()
    val window = minecraft.window
    val guiScale = window.guiScale.toFloat()
    val density = 1f / guiScale

    LaunchedEffect(coordinates, guiScale) {
        coordinates?.let { coordinates ->
            while (isActive) {
                withFrameNanos {
                    surface.record {
                        if (!coordinates.isAttached) return@record
                        val position = coordinates.positionInWindow()
                        val bounds = coordinates.boundsInWindow()
                        val height = window.height
                        
                        GL30.glEnable(GL30.GL_SCISSOR_TEST)
                        GL30.glScissor(
                            bounds.left.toInt(),
                            height - (bounds.top + bounds.height).toInt(),
                            bounds.width.toInt(),
                            bounds.height.toInt()
                        )
                        
                        pose().pushPose()
                        
                        val guiX = position.x * density
                        val guiY = position.y * density
                        pose().translate(guiX, guiY, 0f)
                        
                        val itemSizeGui = 16f
                        val boundsWidthGui = bounds.width * density
                        val boundsHeightGui = bounds.height * density
                        val scale = min(boundsWidthGui / itemSizeGui, boundsHeightGui / itemSizeGui)
                        
                        val offsetX = (boundsWidthGui - itemSizeGui * scale) / 2f
                        val offsetY = (boundsHeightGui - itemSizeGui * scale) / 2f
                        
                        pose().translate(offsetX, offsetY, 0f)
                        pose().scale(scale, scale, 1f)
                        
                        renderFakeItem(item, 0, 0)
                        if (decorations) renderItemDecorations(minecraft.font, item, 0, 0)
                        pose().popPose()
                        GL30.glDisable(GL30.GL_SCISSOR_TEST)
                    }
                }
            }
        }
    }
    Spacer(modifier.fillMaxSize().onGloballyPositioned { coordinates = it })
}
