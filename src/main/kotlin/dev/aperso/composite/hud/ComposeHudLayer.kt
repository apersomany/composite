package dev.aperso.composite.hud

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor

fun interface ComposeHudLayer {
    fun extractRenderState(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker)
}
