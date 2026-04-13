package dev.aperso.composite.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import dev.aperso.composite.core.ComposeHud
import dev.aperso.composite.hud.ComposeHudRegistry
import dev.aperso.composite.hud.HudLayerPosition
import dev.aperso.composite.hud.VanillaHud
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

object HudTest {
    private val id = Identifier.fromNamespaceAndPath("composite", "test_hud")
    private var registered = false
    private var enabled by mutableStateOf(false)

    private val hud = ComposeHud {
        if (enabled) Content()
    }

    @Composable
    private fun Content() {
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Composite HUD Test",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

    fun <S> register(): LiteralArgumentBuilder<S> {
        return LiteralArgumentBuilder.literal<S>("hud").executes {
            Minecraft.getInstance().execute {
                if (!registered) {
                    ComposeHudRegistry.get().register(id, HudLayerPosition.after(VanillaHud.HOTBAR), hud)
                    registered = true
                }
                enabled = !enabled
                val state = if (enabled) "enabled" else "disabled"
                Minecraft.getInstance().player?.sendSystemMessage(
                    Component.literal("[Composite] HUD test $state")
                )
            }
            1
        }
    }
}
