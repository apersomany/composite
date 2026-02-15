package dev.aperso.composite.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aperso.composite.component.Components
import dev.aperso.composite.i18n.translate

object TranslationTest : TestCommand("translation") {
    override val content: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Translation System Test",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Text(
                text = "Plain translate():",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = translate("block.minecraft.diamond_block"),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = translate("item.minecraft.diamond_sword"),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = translate("gui.toTitle"),
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "TranslatableText component:",
                style = MaterialTheme.typography.titleMedium
            )
            
            Components.TranslatableText(
                key = "block.minecraft.emerald_block"
            )
            
            Components.TranslatableText(
                key = "death.attack.player",
                args = arrayOf("Steve", "Alex")
            )
            
            Text(
                text = "TranslatableAnnotatedText (rich text):",
                style = MaterialTheme.typography.titleMedium
            )
            
            Components.TranslatableAnnotatedText(
                key = "chat.type.advancement.task",
                style = TextStyle(fontSize = 16.sp),
                args = arrayOf("Player", "Advancement!")
            )
        }
    }
}
