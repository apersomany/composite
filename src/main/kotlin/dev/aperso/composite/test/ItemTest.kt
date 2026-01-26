package dev.aperso.composite.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.aperso.composite.component.Components
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object ItemTest : TestCommand("item") {
    override val content: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Components.Item(ItemStack(Items.DIAMOND_SWORD), Modifier.size(64.dp))
            Components.Item(ItemStack(Items.APPLE).copyWithCount(8), Modifier.size(64.dp))
            Components.Item(ItemStack(Items.GRASS_BLOCK).copyWithCount(64), Modifier.size(64.dp))
        }
    }
}
