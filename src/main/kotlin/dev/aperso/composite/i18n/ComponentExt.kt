package dev.aperso.composite.i18n

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.*
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents

/**
 * Converts a Minecraft [Component] to a Compose [AnnotatedString].
 * 
 * This extension function preserves rich text styling from the Minecraft component,
 * including colors, bold, italic, underline, and strikethrough formatting.
 * The styling is converted to appropriate [SpanStyle] annotations.
 * 
 * @receiver The Minecraft [Component] to convert
 * @return An [AnnotatedString] with preserved styling
 * 
 * @see Style.toSpanStyle
 * @see translateAnnotated
 * 
 * @example
 * ```kotlin
 * val component = Component.translatable("chat.type.advancement.task", "Player", "Advancement!")
 * val annotated = component.toAnnotatedString()
 * Text(text = annotated)
 * ```
 */
fun Component.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    appendComponent(this@toAnnotatedString)
}

private fun AnnotatedString.Builder.appendComponent(component: Component, inheritedStyle: SpanStyle = SpanStyle()) {
    val style = component.style.toSpanStyle().merge(inheritedStyle)
    val contents = component.contents

    when (contents) {
        is PlainTextContents -> {
            withStyle(style) { append(contents.text()) }
        }
        is TranslatableContents -> {
            withStyle(style) {
                val args = contents.args.map { arg ->
                    when (arg) {
                        is Component -> arg.string
                        else -> arg.toString()
                    }
                }.toTypedArray()
                append(Component.translatable(contents.key, *contents.args).string)
            }
        }
        else -> {
            withStyle(style) { append(component.string) }
        }
    }

    component.siblings.forEach { sibling ->
        appendComponent(sibling, style)
    }
}

/**
 * Converts a Minecraft [Style] to a Compose [SpanStyle].
 * 
 * This extension function maps Minecraft text styling properties to their
 * Compose equivalents:
 * - Color → [Color]
 * - Bold → [FontWeight.Bold]
 * - Italic → [FontStyle.Italic]
 * - Underlined → [TextDecoration.Underline][androidx.compose.ui.text.style.TextDecoration.Underline]
 * - Strikethrough → [TextDecoration.LineThrough][androidx.compose.ui.text.style.TextDecoration.LineThrough]
 * 
 * @receiver The Minecraft [Style] to convert
 * @return A [SpanStyle] with equivalent styling properties
 * 
 * @see Component.toAnnotatedString
 */
fun Style.toSpanStyle(): SpanStyle {
    val spanStyle = SpanStyle()
    val modifiers = mutableListOf<SpanStyle.() -> SpanStyle>()

    this.color?.let { textColor ->
        modifiers.add { copy(color = Color(textColor.value)) }
    }

    if (this.isBold) modifiers.add { copy(fontWeight = FontWeight.Bold) }
    if (this.isItalic) modifiers.add { copy(fontStyle = FontStyle.Italic) }
    if (this.isUnderlined) modifiers.add { copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline) }
    if (this.isStrikethrough) modifiers.add { copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) }

    return modifiers.fold(spanStyle) { acc, modifier -> acc.modifier() }
}

/**
 * Converts a Minecraft [ChatFormatting] color code to a Compose [Color].
 * 
 * Maps all 16 standard Minecraft chat colors to their RGB equivalents.
 * 
 * @receiver The [ChatFormatting] to convert, or null
 * @return The corresponding [Color], or null if the input is null or not a color formatting code
 * 
 * @see Style.toSpanStyle
 */
fun ChatFormatting?.toColor(): Color? = when (this) {
    ChatFormatting.BLACK -> Color(0x000000)
    ChatFormatting.DARK_BLUE -> Color(0x0000AA)
    ChatFormatting.DARK_GREEN -> Color(0x00AA00)
    ChatFormatting.DARK_AQUA -> Color(0x00AAAA)
    ChatFormatting.DARK_RED -> Color(0xAA0000)
    ChatFormatting.DARK_PURPLE -> Color(0xAA00AA)
    ChatFormatting.GOLD -> Color(0xFFAA00)
    ChatFormatting.GRAY -> Color(0xAAAAAA)
    ChatFormatting.DARK_GRAY -> Color(0x555555)
    ChatFormatting.BLUE -> Color(0x5555FF)
    ChatFormatting.GREEN -> Color(0x55FF55)
    ChatFormatting.AQUA -> Color(0x55FFFF)
    ChatFormatting.RED -> Color(0xFF5555)
    ChatFormatting.LIGHT_PURPLE -> Color(0xFF55FF)
    ChatFormatting.YELLOW -> Color(0xFFFF55)
    ChatFormatting.WHITE -> Color(0xFFFFFF)
    else -> null
}
