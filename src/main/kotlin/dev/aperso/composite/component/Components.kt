package dev.aperso.composite.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import dev.aperso.composite.i18n.translate
import dev.aperso.composite.i18n.translateAnnotated
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

/**
 * A collection of Compose components for rendering Minecraft content.
 * 
 * This object provides composable functions for integrating Minecraft-specific
 * elements like items, textures, and translations into your Compose UI.
 */
object Components {
    /**
     * Renders a Minecraft [ItemStack] within a Compose layout.
     * 
     * @param item The ItemStack to render
     * @param modifier The modifier to apply to the item container
     * @param decorations Whether to render item decorations (enchantment glint, durability bar, etc.)
     * @param tooltip Whether to show the item tooltip on hover
     */
    @Composable
    fun Item(
        item: ItemStack,
        modifier: Modifier = Modifier,
        decorations: Boolean = true,
        tooltip: Boolean = true
    ) = dev.aperso.composite.component.Item(item, modifier, decorations, tooltip)

    /**
     * Renders a Minecraft texture from a [ResourceLocation] within a Compose layout.
     * 
     * @param texture The ResourceLocation of the texture to render
     * @param modifier The modifier to apply to the texture container
     * @param u The starting U coordinate (horizontal) as a fraction (0.0-1.0)
     * @param v The starting V coordinate (vertical) as a fraction (0.0-1.0)
     * @param w The width of the texture region as a fraction (0.0-1.0)
     * @param h The height of the texture region as a fraction (0.0-1.0)
     */
    @Composable
    fun Texture(
        texture: ResourceLocation,
        modifier: Modifier = Modifier,
        u: Float = 0f,
        v: Float = 0f,
        w: Float = 1f,
        h: Float = 1f
    ) = dev.aperso.composite.component.Texture(texture, modifier, u, v, w, h)

    /**
     * Renders a Minecraft texture from an [AbstractTexture] within a Compose layout.
     * 
     * @param texture The AbstractTexture to render
     * @param modifier The modifier to apply to the texture container
     * @param u The starting U coordinate (horizontal) as a fraction (0.0-1.0)
     * @param v The starting V coordinate (vertical) as a fraction (0.0-1.0)
     * @param w The width of the texture region as a fraction (0.0-1.0)
     * @param h The height of the texture region as a fraction (0.0-1.0)
     */
    @Composable
    fun Texture(
        texture: AbstractTexture,
        modifier: Modifier = Modifier,
        u: Float = 0f,
        v: Float = 0f,
        w: Float = 1f,
        h: Float = 1f
    ) = dev.aperso.composite.component.Texture(texture, modifier, u, v, w, h)

    /**
     * Loads and renders a Minecraft asset image from a [ResourceLocation].
     * 
     * @param resource The ResourceLocation of the image asset
     * @param modifier The modifier to apply to the image container
     * @param contentDescription Accessibility description for the image
     * @param alignment How the image should be aligned within its container
     * @param contentScale How the image should be scaled within its container
     * @param alpha The opacity of the image (0.0-1.0)
     * @param colorFilter Optional color filter to apply to the image
     * @param filterQuality The filtering quality for scaling
     */
    @Composable
    fun AssetImage(
        resource: ResourceLocation,
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
        alignment: Alignment = Alignment.Center,
        contentScale: ContentScale = ContentScale.Fit,
        alpha: Float = DefaultAlpha,
        colorFilter: ColorFilter? = null,
        filterQuality: FilterQuality = FilterQuality.None,
    ) = dev.aperso.composite.component.AssetImage(
        resource,
        modifier,
        contentDescription,
        alignment,
        contentScale,
        alpha,
        colorFilter,
        filterQuality
    )

    /**
     * A Material 3 Text component that displays a translated Minecraft string.
     * 
     * This component wraps [androidx.compose.material3.Text] and automatically
     * translates the provided key using Minecraft's localization system.
     * The translation will update when the player changes their language.
     * 
     * For rich text support (preserving colors and formatting from language files),
     * use [TranslatableAnnotatedText] instead.
     * 
     * @param key The Minecraft translation key (e.g., "block.minecraft.diamond_block")
     * @param modifier The modifier to apply to the text
     * @param color The text color
     * @param fontSize The font size
     * @param fontStyle The font style (normal or italic)
     * @param fontWeight The font weight
     * @param fontFamily The font family
     * @param letterSpacing The letter spacing
     * @param textDecoration The text decoration (underline, strikethrough)
     * @param textAlign The text alignment
     * @param lineHeight The line height
     * @param overflow How text overflow should be handled
     * @param softWrap Whether text should wrap at line breaks
     * @param maxLines The maximum number of lines
     * @param minLines The minimum number of lines
     * @param args Arguments to substitute into the translation string
     * 
     * @see translate
     * @see TranslatableAnnotatedText
     * 
     * @example
     * ```kotlin
     * Components.TranslatableText(
     *     key = "block.minecraft.diamond_block"
     * )
     * 
     * Components.TranslatableText(
     *     key = "death.attack.player",
     *     args = arrayOf("Steve", "Alex"),
     *     color = Color.Red
     * )
     * ```
     */
    @Composable
    fun TranslatableText(
        key: String,
        modifier: Modifier = Modifier,
        color: Color = Color.Unspecified,
        fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
        fontStyle: androidx.compose.ui.text.font.FontStyle? = null,
        fontWeight: androidx.compose.ui.text.font.FontWeight? = null,
        fontFamily: androidx.compose.ui.text.font.FontFamily? = null,
        letterSpacing: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
        textDecoration: androidx.compose.ui.text.style.TextDecoration? = null,
        textAlign: androidx.compose.ui.text.style.TextAlign = androidx.compose.ui.text.style.TextAlign.Start,
        lineHeight: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
        overflow: TextOverflow = TextOverflow.Clip,
        softWrap: Boolean = true,
        maxLines: Int = Int.MAX_VALUE,
        minLines: Int = 1,
        args: Array<out Any> = emptyArray()
    ) {
        androidx.compose.material3.Text(
            text = translate(key, *args),
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines
        )
    }

    /**
     * A text component that displays a translated Minecraft string with preserved rich text styling.
     * 
     * This component translates the provided key and preserves any styling from
     * the Minecraft language files (colors, bold, italic, underline, strikethrough).
     * The styling is converted to Compose [SpanStyle][androidx.compose.ui.text.SpanStyle] annotations.
     * 
     * When [onClick] is provided, the text becomes clickable and the callback receives
     * the character offset that was clicked.
     * 
     * For simpler use cases without rich text support, use [TranslatableText] instead.
     * 
     * @param key The Minecraft translation key (e.g., "chat.type.advancement.task")
     * @param modifier The modifier to apply to the text
     * @param style The base text style to apply
     * @param softWrap Whether text should wrap at line breaks
     * @param overflow How text overflow should be handled
     * @param maxLines The maximum number of lines
     * @param onClick Optional click handler, receives the character offset clicked
     * @param args Arguments to substitute into the translation string
     * 
     * @see translateAnnotated
     * @see TranslatableText
     * 
     * @example
     * ```kotlin
     * Components.TranslatableAnnotatedText(
     *     key = "chat.type.advancement.task",
     *     args = arrayOf("Player", "Advancement!"),
     *     style = TextStyle(fontSize = 16.sp)
     * )
     * 
     * Components.TranslatableAnnotatedText(
     *     key = "chat.type.advancement.task",
     *     args = arrayOf("Player", "Advancement!"),
     *     onClick = { offset -> println("Clicked at offset $offset") }
     * )
     * ```
     */
    @Composable
    @Suppress("DEPRECATION")
    fun TranslatableAnnotatedText(
        key: String,
        modifier: Modifier = Modifier,
        style: TextStyle = TextStyle.Default,
        softWrap: Boolean = true,
        overflow: TextOverflow = TextOverflow.Clip,
        maxLines: Int = Int.MAX_VALUE,
        onClick: ((Int) -> Unit)? = null,
        args: Array<out Any> = emptyArray()
    ) {
        val annotatedString = translateAnnotated(key, *args)
        if (onClick != null) {
            ClickableText(
                text = annotatedString,
                modifier = modifier,
                style = style,
                softWrap = softWrap,
                overflow = overflow,
                maxLines = maxLines,
                onClick = onClick
            )
        } else {
            androidx.compose.foundation.text.BasicText(
                text = annotatedString,
                modifier = modifier,
                style = style,
                softWrap = softWrap,
                overflow = overflow,
                maxLines = maxLines
            )
        }
    }
}