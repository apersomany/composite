package dev.aperso.composite.i18n

import androidx.compose.runtime.compositionLocalOf
import net.minecraft.client.Minecraft

/**
 * CompositionLocal that provides the current Minecraft language code.
 * 
 * This value is automatically provided by [ComposeGui][dev.aperso.composite.core.ComposeGui]
 * and will trigger recomposition when the player changes their language setting.
 * 
 * @see translate
 */
val LocalLocale = compositionLocalOf<String> {
    Minecraft.getInstance().options.languageCode ?: "en_us"
}
