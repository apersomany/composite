package dev.aperso.composite.hud.fabric

import dev.aperso.composite.hud.ComposeHudLayer
import dev.aperso.composite.hud.ComposeHudRegistry
import dev.aperso.composite.hud.HudLayerPosition
import dev.aperso.composite.hud.VanillaHud
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier

class FabricComposeHudRegistry : ComposeHudRegistry {
    companion object {
        private val ID_MAP = mapOf(
            // Camera / Misc overlays
            VanillaHud.MISC_OVERLAYS to VanillaHudElements.MISC_OVERLAYS,

            // Crosshair
            VanillaHud.CROSSHAIR to VanillaHudElements.CROSSHAIR,

            // Spectator
            VanillaHud.SPECTATOR_MENU to VanillaHudElements.SPECTATOR_MENU,
            VanillaHud.SPECTATOR_TOOLTIP to VanillaHudElements.SPECTATOR_TOOLTIP,

            // Hotbar & items
            VanillaHud.HOTBAR to VanillaHudElements.HOTBAR,
            VanillaHud.HELD_ITEM_TOOLTIP to VanillaHudElements.HELD_ITEM_TOOLTIP,

            // Player status bars
            VanillaHud.HEALTH_BAR to VanillaHudElements.HEALTH_BAR,
            VanillaHud.ARMOR_BAR to VanillaHudElements.ARMOR_BAR,
            VanillaHud.FOOD_BAR to VanillaHudElements.FOOD_BAR,
            VanillaHud.AIR_BAR to VanillaHudElements.AIR_BAR,
            VanillaHud.MOUNT_HEALTH to VanillaHudElements.MOUNT_HEALTH,

            // Experience
            VanillaHud.INFO_BAR to VanillaHudElements.INFO_BAR,
            VanillaHud.EXPERIENCE_LEVEL to VanillaHudElements.EXPERIENCE_LEVEL,

            // Effects & overlays
            VanillaHud.MOB_EFFECTS to VanillaHudElements.MOB_EFFECTS,
            VanillaHud.BOSS_BAR to VanillaHudElements.BOSS_BAR,
            VanillaHud.SLEEP to VanillaHudElements.SLEEP,
            VanillaHud.DEMO_TIMER to VanillaHudElements.DEMO_TIMER,

            // Scoreboard & messages
            VanillaHud.SCOREBOARD to VanillaHudElements.SCOREBOARD,
            VanillaHud.OVERLAY_MESSAGE to VanillaHudElements.OVERLAY_MESSAGE,
            VanillaHud.TITLE_AND_SUBTITLE to VanillaHudElements.TITLE_AND_SUBTITLE,

            // Chat & player list
            VanillaHud.CHAT to VanillaHudElements.CHAT,
            VanillaHud.PLAYER_LIST to VanillaHudElements.PLAYER_LIST,
            VanillaHud.SUBTITLES to VanillaHudElements.SUBTITLES,
        )
    }

    override fun register(id: Identifier, position: HudLayerPosition, layer: ComposeHudLayer) {
        val vanillaId = ID_MAP[position.vanillaElement]
            ?: throw IllegalArgumentException("Unknown vanilla element: ${position.vanillaElement}")

        val element = { graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker ->
            layer.extractRenderState(graphics, deltaTracker)
        }

        when (position.type) {
            HudLayerPosition.Type.BEFORE -> HudElementRegistry.attachElementBefore(vanillaId, id, element)
            HudLayerPosition.Type.AFTER -> HudElementRegistry.attachElementAfter(vanillaId, id, element)
        }
    }
}
