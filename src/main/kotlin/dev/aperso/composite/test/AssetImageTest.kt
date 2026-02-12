package dev.aperso.composite.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aperso.composite.component.Components
import net.minecraft.resources.ResourceLocation

object AssetImageTest : TestCommand("asset_image") {
    override val content: @Composable () -> Unit = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Components.AssetImage(
                resource = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dirt.png"),
                modifier = Modifier.size(128.dp)
            )
        }
    }
}
