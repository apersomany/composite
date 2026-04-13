package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import dev.aperso.composite.hud.ComposeHudLayer
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

open class ComposeHud(content: @Composable () -> Unit) : ComposeHudLayer {
    val gui = ComposeGui(content)

    override fun extractRenderState(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        gui.init()
        gui.extractRenderState(graphics, 0, 0, deltaTracker.getGameTimeDeltaPartialTick(true))
    }

    fun close() {
        gui.onClose()
    }
}
