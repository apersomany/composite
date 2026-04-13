package dev.aperso.composite.hud

data class HudLayerPosition(val type: Type, val vanillaElement: String) {
    enum class Type {
        BEFORE,
        AFTER
    }

    companion object {
        fun before(element: String) = HudLayerPosition(Type.BEFORE, element)
        fun after(element: String) = HudLayerPosition(Type.AFTER, element)
    }
}
