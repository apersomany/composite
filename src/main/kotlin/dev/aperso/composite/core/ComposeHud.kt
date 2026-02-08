package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import dev.aperso.composite.skia.LocalSkiaSurface
import dev.aperso.composite.skia.SkiaSurface
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import org.jetbrains.skiko.currentNanoTime

@OptIn(InternalComposeUiApi::class, ExperimentalComposeUiApi::class)
open class ComposeHud(content: @Composable () -> Unit): HudRenderCallback {
    private val scene = CanvasLayersComposeScene()
    private val surface = SkiaSurface()

    init {
        scene.setContent {
            CompositionLocalProvider(LocalSkiaSurface provides surface) {
                content()
            }
        }
    }

    override fun onHudRender(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker) {
        val window = Minecraft.getInstance().window
        scene.size = IntSize(window.width, window.height)
        scene.density = Density(window.guiScale.toFloat())
        surface.resize(window.width, window.height)
        surface.render(guiGraphics) {
            scene.render(it, currentNanoTime())
        }
    }
}