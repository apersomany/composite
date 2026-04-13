package dev.aperso.composite.hud

import net.minecraft.resources.Identifier

interface ComposeHudRegistry {
    fun register(id: Identifier, position: HudLayerPosition, layer: ComposeHudLayer)

    companion object {
        fun get(): ComposeHudRegistry = ComposeHudRegistryHolder.instance
    }
}
