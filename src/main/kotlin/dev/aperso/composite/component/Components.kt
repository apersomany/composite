package dev.aperso.composite.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

object Components {
    @Composable
    fun Item(
        item: ItemStack,
        modifier: Modifier = Modifier,
        decorations: Boolean = true,
        tooltip: Boolean = true
    ) = dev.aperso.composite.component.Item(item, modifier, decorations, tooltip)

    @Composable
    fun Texture(
        texture: ResourceLocation,
        modifier: Modifier = Modifier,
        u: Float = 0f,
        v: Float = 0f,
        w: Float = 1f,
        h: Float = 1f
    ) = dev.aperso.composite.component.Texture(texture, modifier, u, v, w, h)

    @Composable
    fun Texture(
        texture: AbstractTexture,
        modifier: Modifier = Modifier,
        u: Float = 0f,
        v: Float = 0f,
        w: Float = 1f,
        h: Float = 1f
    ) = dev.aperso.composite.component.Texture(texture, modifier, u, v, w, h)
}