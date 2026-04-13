package dev.aperso.composite.hud.neoforge

import dev.aperso.composite.hud.ComposeHudLayer
import dev.aperso.composite.hud.ComposeHudRegistry
import dev.aperso.composite.hud.HudLayerPosition
import dev.aperso.composite.hud.VanillaHud
import net.minecraft.resources.Identifier
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge

class NeoForgeComposeHudRegistry : ComposeHudRegistry {
    companion object {
        private val ID_MAP = mapOf(
            // Camera / Misc overlays
            VanillaHud.CAMERA_OVERLAYS to VanillaGuiLayers.CAMERA_OVERLAYS,
            VanillaHud.MISC_OVERLAYS to VanillaGuiLayers.CAMERA_OVERLAYS,

            // Crosshair
            VanillaHud.CROSSHAIR to VanillaGuiLayers.CROSSHAIR,

            // Spectator
            VanillaHud.SPECTATOR_TOOLTIP to VanillaGuiLayers.SPECTATOR_TOOLTIP,

            // Hotbar & items
            VanillaHud.HOTBAR to VanillaGuiLayers.HOTBAR,
            VanillaHud.HELD_ITEM_TOOLTIP to VanillaGuiLayers.SELECTED_ITEM_NAME,
            VanillaHud.SELECTED_ITEM_NAME to VanillaGuiLayers.SELECTED_ITEM_NAME,

            // Player status bars
            VanillaHud.HEALTH_BAR to VanillaGuiLayers.PLAYER_HEALTH,
            VanillaHud.ARMOR_BAR to VanillaGuiLayers.ARMOR_LEVEL,
            VanillaHud.FOOD_BAR to VanillaGuiLayers.FOOD_LEVEL,
            VanillaHud.AIR_BAR to VanillaGuiLayers.AIR_LEVEL,
            VanillaHud.MOUNT_HEALTH to VanillaGuiLayers.VEHICLE_HEALTH,

            // Experience
            VanillaHud.INFO_BAR to VanillaGuiLayers.CONTEXTUAL_INFO_BAR,
            VanillaHud.EXPERIENCE_LEVEL to VanillaGuiLayers.EXPERIENCE_LEVEL,

            // Effects & overlays
            VanillaHud.MOB_EFFECTS to VanillaGuiLayers.EFFECTS,
            VanillaHud.BOSS_BAR to VanillaGuiLayers.BOSS_OVERLAY,
            VanillaHud.SLEEP to VanillaGuiLayers.SLEEP_OVERLAY,
            VanillaHud.DEMO_TIMER to VanillaGuiLayers.DEMO_OVERLAY,

            // Scoreboard & messages
            VanillaHud.SCOREBOARD to VanillaGuiLayers.SCOREBOARD_SIDEBAR,
            VanillaHud.OVERLAY_MESSAGE to VanillaGuiLayers.OVERLAY_MESSAGE,
            VanillaHud.TITLE_AND_SUBTITLE to VanillaGuiLayers.TITLE,

            // Chat & player list
            VanillaHud.CHAT to VanillaGuiLayers.CHAT,
            VanillaHud.PLAYER_LIST to VanillaGuiLayers.TAB_LIST,
            VanillaHud.SUBTITLES to VanillaGuiLayers.SUBTITLE_OVERLAY,
        )
    }

    override fun register(id: Identifier, position: HudLayerPosition, layer: ComposeHudLayer) {
        val vanillaId = ID_MAP[position.vanillaElement]
            ?: throw IllegalArgumentException("Unknown vanilla element: ${position.vanillaElement}")

        when (position.type) {
            HudLayerPosition.Type.BEFORE -> NeoForge.EVENT_BUS.addListener(RenderGuiLayerEvent.Pre::class.java) { event ->
                if (event.name == vanillaId) {
                    layer.extractRenderState(event.guiGraphics, event.partialTick)
                }
            }
            HudLayerPosition.Type.AFTER -> NeoForge.EVENT_BUS.addListener(RenderGuiLayerEvent.Post::class.java) { event ->
                if (event.name == vanillaId) {
                    layer.extractRenderState(event.guiGraphics, event.partialTick)
                }
            }
        }
    }
}