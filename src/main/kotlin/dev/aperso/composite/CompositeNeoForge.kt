package dev.aperso.composite

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.aperso.composite.hud.ComposeHudRegistryHolder
import dev.aperso.composite.hud.neoforge.NeoForgeComposeHudRegistry
import dev.aperso.composite.skia.SkiaContext
import dev.aperso.composite.test.AssetImageTest
import dev.aperso.composite.test.HudTest
import dev.aperso.composite.test.ItemTest
import dev.aperso.composite.test.TextureTest
import dev.aperso.composite.test.TranslationTest
import net.minecraft.commands.CommandSourceStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent
import net.neoforged.neoforge.common.NeoForge
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Mod("composite", dist = [Dist.CLIENT])
class CompositeNeoForge {
    val logger: Logger = LoggerFactory.getLogger("Composite")

    init {
        ComposeHudRegistryHolder.init(NeoForgeComposeHudRegistry())

        NeoForge.EVENT_BUS.addListener { _: ClientStartedEvent ->
            logger.info("initializing skia context")
            SkiaContext.initialize()
        }

        NeoForge.EVENT_BUS.addListener { event: RegisterClientCommandsEvent ->
            event.dispatcher.register(
                LiteralArgumentBuilder.literal<CommandSourceStack>("composite")
                    .then(
                        LiteralArgumentBuilder.literal<CommandSourceStack>("test")
                            .then(TextureTest.register())
                            .then(ItemTest.register())
                            .then(AssetImageTest.register())
                            .then(TranslationTest.register())
                            .then(HudTest.register())
                    )
            )
        }
    }
}
