package dev.aperso.composite.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import net.minecraft.network.chat.Component

/**
 * Translates a Minecraft translation key to a localized string.
 * 
 * This composable function retrieves the translation for the given key using
 * the player's current language setting. It will automatically recompose
 * when the language changes.
 * 
 * @param key The translation key (e.g., "block.minecraft.diamond_block")
 * @param args Arguments to substitute into the translation string. Supports
 *             [Component], [String], [Number], and any other type (converted via toString())
 * @return The translated string, or the key itself if no translation is found
 * 
 * @see translateOrNull
 * @see translateAnnotated
 * @see TranslatableText
 * 
 * @example
 * ```kotlin
 * Text(text = translate("block.minecraft.diamond_block"))
 * Text(text = translate("death.attack.player", arrayOf("Steve", "Alex")))
 * ```
 */
@Composable
fun translate(key: String, vararg args: Any): String {
    val locale = LocalLocale.current
    return remember(key, args.toList(), locale) {
        translateDirect(key, *args)
    }
}

/**
 * Translates a Minecraft translation key, returning null if no translation exists.
 * 
 * Unlike [translate], this function returns null instead of the key itself
 * when no translation is found for the given key.
 * 
 * @param key The translation key to look up
 * @param args Arguments to substitute into the translation string
 * @return The translated string, or null if no translation exists
 * 
 * @see translate
 */
@Composable
fun translateOrNull(key: String, vararg args: Any): String? {
    val locale = LocalLocale.current
    return remember(key, args.toList(), locale) {
        try {
            val component = createTranslatableComponent(key, *args)
            val result = component.string
            if (result != key) result else null
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Translates a Minecraft translation key to an [AnnotatedString] with preserved styling.
 * 
 * This function converts the translated text including any rich text formatting
 * (colors, bold, italic, underline, strikethrough) from the Minecraft component
 * to Compose's [AnnotatedString] with appropriate [SpanStyle][androidx.compose.ui.text.SpanStyle] annotations.
 * 
 * @param key The translation key (e.g., "chat.type.advancement.task")
 * @param args Arguments to substitute into the translation string
 * @return An [AnnotatedString] with preserved styling from the translation
 * 
 * @see translate
 * @see TranslatableAnnotatedText
 * @see Component.toAnnotatedString
 * 
 * @example
 * ```kotlin
 * val annotated = translateAnnotated("chat.type.advancement.task", arrayOf("Player", "Advancement!"))
 * Text(text = annotated)
 * ```
 */
@Composable
fun translateAnnotated(key: String, vararg args: Any): AnnotatedString {
    val locale = LocalLocale.current
    return remember(key, args.toList(), locale) {
        translateAnnotatedDirect(key, *args)
    }
}

/**
 * Translates a Minecraft translation key without composable context.
 * 
 * Use this function when you need a translation outside of a composable scope.
 * Note that this does not automatically update when the language changes.
 * 
 * @param key The translation key
 * @param args Arguments to substitute into the translation string
 * @return The translated string, or the key itself if no translation is found
 * 
 * @see translate
 */
fun translateDirect(key: String, vararg args: Any): String {
    val component = createTranslatableComponent(key, *args)
    return component.string
}

/**
 * Translates a Minecraft translation key to an [AnnotatedString] without composable context.
 * 
 * Use this function when you need a rich-text translation outside of a composable scope.
 * Note that this does not automatically update when the language changes.
 * 
 * @param key The translation key
 * @param args Arguments to substitute into the translation string
 * @return An [AnnotatedString] with preserved styling from the translation
 * 
 * @see translateAnnotated
 */
fun translateAnnotatedDirect(key: String, vararg args: Any): AnnotatedString {
    val component = createTranslatableComponent(key, *args)
    return component.toAnnotatedString()
}

private fun createTranslatableComponent(key: String, vararg args: Any): Component {
    val mcArgs = args.map { arg ->
        when (arg) {
            is Component -> arg
            is String -> Component.literal(arg)
            is Number -> Component.literal(arg.toString())
            else -> Component.literal(arg.toString())
        }
    }.toTypedArray()
    
    return Component.translatable(key, *mcArgs)
}
