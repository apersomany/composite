package dev.aperso.composite

import dev.aperso.composite.skia.SkiaContext
import dev.aperso.composite.test.AssetImageTest
import dev.aperso.composite.test.ItemTest
import dev.aperso.composite.test.TextureTest
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Composite : ClientModInitializer {
    val logger: Logger = LoggerFactory.getLogger("Composite")

    override fun onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register {
            logger.info("initializing skia context")
            SkiaContext.initialize()
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("composite")
                    .then(
                        ClientCommandManager.literal("test")
                            .then(TextureTest.register())
                            .then(ItemTest.register())
                            .then(AssetImageTest.register())
                    )
            )
        }
    }
}
