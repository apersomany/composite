# Composite

**Composite** is a Minecraft Fabric library that brings **Jetpack Compose** and **Material 3** to Minecraft modding. It allows developers to build modern, reactive user interfaces for Minecraft Screens and HUDs using the declarative power of Kotlin and Compose.

## Features

*   **Jetpack Compose UI**: Build Minecraft GUIs using standard Compose code.
*   **Material 3**: Full support for Material Design 3 components.
*   **Minecraft Integration**:
    *   **Screens**: Create full-screen UIs with `ComposeScreen`.
    *   **HUD**: Render overlays with `ComposeHud`.
    *   **Items**: Render Minecraft ItemStacks inside Compose layouts.
    *   **Textures**: Render Minecraft textures (ResourceLocations) inside Compose layouts.
    *   **Translations**: Integrated i18n support with rich text styling preservation.
*   **Input Handling**: Automatic mapping of Minecraft mouse and keyboard events to Compose.

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("org.jetbrains.compose") version "1.9.3"
}

repositories {
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(compose.desktop.windows_x64)
    implementation(compose.material3)
    modImplementation("com.github.apersomany:composite:0.5.0")
}
```

## Usage

### Creating a Screen

To create a custom screen, instantiate `ComposeScreen` and pass your Composable content. You can then open it using `Minecraft.getInstance().setScreen(...)`.

```kotlin
import dev.aperso.composite.core.ComposeScreen
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.minecraft.client.Minecraft

fun openMyScreen() {
    Minecraft.getInstance().setScreen(ComposeScreen {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { println("Clicked!") }) {
                Text("Hello from Compose!")
            }
        }
    })
}
```

### Creating a HUD Overlay

To render a HUD overlay, register a `ComposeHud` instance with Fabric's `HudRenderCallback`.

```kotlin
import dev.aperso.composite.core.ComposeHud
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

// In your ClientModInitializer
override fun onInitializeClient() {
    HudRenderCallback.EVENT.register(ComposeHud {
        Text(
            text = "HUD Overlay", 
            color = Color.White
        )
    })
}
```

### Rendering Items

You can render Minecraft items within your Compose UI using the `Components.Item` composable.

```kotlin
import dev.aperso.composite.component.Components
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack

// Inside a Composable
Components.Item(ItemStack(Items.DIAMOND_SWORD))
```

### Rendering Textures

You can render any Minecraft texture using `Components.Texture`.

```kotlin
import dev.aperso.composite.component.Components
import net.minecraft.resources.ResourceLocation
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

// Inside a Composable
Components.Texture(
    texture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/dirt.png"),
    modifier = Modifier.size(64.dp)
)
```

### Translations

Composite provides integrated i18n support that bridges Minecraft's translation system with Compose.

#### Basic Translation

Use `translate()` for simple string translation:

```kotlin
import dev.aperso.composite.i18n.translate
import androidx.compose.material3.Text

// Inside a Composable
Text(text = translate("block.minecraft.diamond_block"))
Text(text = translate("death.attack.player", arrayOf("Steve", "Alex")))
```

#### TranslatableText Component

Use `Components.TranslatableText` for Material 3 styled text:

```kotlin
import dev.aperso.composite.component.Components

// Inside a Composable
Components.TranslatableText(
    key = "block.minecraft.diamond_block"
)

Components.TranslatableText(
    key = "death.attack.player",
    args = arrayOf("Steve", "Alex"),
    color = androidx.compose.ui.graphics.Color.Red
)
```

#### Rich Text Translation

Use `translateAnnotated()` or `Components.TranslatableAnnotatedText` to preserve styling (colors, bold, italic, etc.) from language files:

```kotlin
import dev.aperso.composite.i18n.translateAnnotated
import dev.aperso.composite.component.Components

// Using the function directly
val annotatedString = translateAnnotated("chat.type.advancement.task", arrayOf("Player", "Advancement!"))

// Using the component with click handling
Components.TranslatableAnnotatedText(
    key = "chat.type.advancement.task",
    args = arrayOf("Player", "Advancement!"),
    onClick = { offset -> /* handle click */ }
)
```

Translations automatically update when the player changes their language setting.

## Requirements

*   Minecraft 1.21.1
*   Fabric Loader
*   Fabric Language Kotlin

## License

All Rights Reserved.
