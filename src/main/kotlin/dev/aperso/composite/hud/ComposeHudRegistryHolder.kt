package dev.aperso.composite.hud

object ComposeHudRegistryHolder {
    lateinit var instance: ComposeHudRegistry
        private set

    fun init(impl: ComposeHudRegistry) {
        check(!::instance.isInitialized) { "Already initialized" }
        instance = impl
    }
}
