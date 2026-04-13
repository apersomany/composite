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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import dev.aperso.composite.i18n.translate
import dev.aperso.composite.i18n.translateAnnotated
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

/**
 * A collection of Compose components for rendering Minecraft content.
 * 
 * This object provides composable functions for integrating Minecraft-specific
 * elements like items, textures, and translations into your Compose UI.
 */
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
        texture: Identifier,
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

    @Composable
    fun AssetImage(
        resource: Identifier,
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
        alignment: Alignment = Alignment.Center,
        contentScale: ContentScale = ContentScale.Fit,
        alpha: Float = DefaultAlpha,
        colorFilter: ColorFilter? = null,
        filterQuality: FilterQuality = FilterQuality.None,
    ) = dev.aperso.composite.component.AssetImage(
        resource, modifier, contentDescription, alignment,
        contentScale, alpha, colorFilter, filterQuality
    )

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