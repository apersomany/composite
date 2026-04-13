# Composite

**Composite** is a Minecraft library mod that brings **Jetpack Compose** and **Material 3** to Minecraft modding. It allows developers to build modern, reactive user interfaces for Minecraft Screens and HUDs using the declarative power of Kotlin and Compose.

Supports both **Fabric** and **NeoForge**.

## Features

*   **Jetpack Compose UI**: Build Minecraft GUIs using standard Compose code.
*   **Material 3**: Full support for Material Design 3 components and Material Icons Extended.
*   **Minecraft Integration**:
    *   **Screens**: Create full-screen UIs with `ComposeScreen`.
    *   **HUD**: Render overlays with `ComposeHud` registered via `ComposeHudRegistry`.
    *   **HUD Layering**: Position your HUD before or after any vanilla HUD element using `HudLayerPosition` and `VanillaHud` constants.
    *   **Items**: Render Minecraft ItemStacks inside Compose layouts.
    *   **Textures**: Render Minecraft textures (Identifier) inside Compose layouts.
    *   **Asset Images**: Load and render PNGs from resource packs as Compose `Image`s.
    *   **Translations**: Integrated i18n support with rich text styling preservation.
*   **Input Handling**: Automatic mapping of Minecraft mouse and keyboard events to Compose.
*   **Multi-loader**: Works on both Fabric and NeoForge.

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
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    modImplementation("com.github.apersomany:composite:0.5.0")
}
```

## Usage

### Creating a Screen

Instantiate `ComposeScreen` and pass your composable content, then open it with `Minecraft.getInstance().setScreen(...)`.

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

Register a `ComposeHud` with `ComposeHudRegistry`, specifying where it should be rendered relative to a vanilla HUD element using `HudLayerPosition` and `VanillaHud` constants.

```kotlin
import dev.aperso.composite.core.ComposeHud
import dev.aperso.composite.hud.ComposeHudRegistry
import dev.aperso.composite.hud.HudLayerPosition
import dev.aperso.composite.hud.VanillaHud
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import net.minecraft.resources.Identifier

// In your ClientModInitializer (or NeoForge client setup)
ComposeHudRegistry.get().register(
    id = Identifier.of("mymod", "my_overlay"),
    position = HudLayerPosition.before(VanillaHud.HOTBAR),
    layer = ComposeHud {
        Text(
            text = "HUD Overlay",
            color = Color.White
        )
    }
)
```

#### `HudLayerPosition`

Position your HUD layer relative to any vanilla element:

```kotlin
HudLayerPosition.before(VanillaHud.HOTBAR)       // render before the hotbar
HudLayerPosition.after(VanillaHud.HEALTH_BAR)    // render after the health bar
```

#### `VanillaHud` constants

| Constant | Description |
|---|---|
| `CAMERA_OVERLAYS` / `MISC_OVERLAYS` | Camera and misc overlays |
| `CROSSHAIR` | Crosshair |
| `HOTBAR` / `SELECTED_ITEM_NAME` / `HELD_ITEM_TOOLTIP` | Hotbar area |
| `HEALTH_BAR` / `ARMOR_BAR` / `FOOD_BAR` / `AIR_BAR` / `MOUNT_HEALTH` | Status bars |
| `INFO_BAR` / `EXPERIENCE_LEVEL` | Experience |
| `MOB_EFFECTS` / `BOSS_BAR` / `SLEEP` / `DEMO_TIMER` | Effects and overlays |
| `SCOREBOARD` / `OVERLAY_MESSAGE` / `TITLE_AND_SUBTITLE` | Text overlays |
| `CHAT` / `PLAYER_LIST` / `SUBTITLES` | Chat and player list |

### Rendering Items

```kotlin
import dev.aperso.composite.component.Components
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack

// Inside a Composable
Components.Item(ItemStack(Items.DIAMOND_SWORD))
```

### Rendering Textures

```kotlin
import dev.aperso.composite.component.Components
import net.minecraft.resources.Identifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

// Inside a Composable
Components.Texture(
    texture = Identifier.fromNamespaceAndPath("minecraft", "textures/block/dirt.png"),
    modifier = Modifier.size(64.dp)
)
```

### Asset Images

Load a PNG directly from the resource pack:

```kotlin
import dev.aperso.composite.component.Components
import net.minecraft.resources.Identifier

// Inside a Composable
Components.AssetImage(
    identifier = Identifier.fromNamespaceAndPath("mymod", "textures/gui/icon.png"),
    modifier = Modifier.size(32.dp)
)
```

### Translations

#### Basic Translation

```kotlin
import dev.aperso.composite.i18n.translate
import androidx.compose.material3.Text

// Inside a Composable
Text(text = translate("block.minecraft.diamond_block"))
Text(text = translate("death.attack.player", arrayOf("Steve", "Alex")))
```

#### TranslatableText Component

```kotlin
import dev.aperso.composite.component.Components

// Inside a Composable
Components.TranslatableText(key = "block.minecraft.diamond_block")

Components.TranslatableText(
    key = "death.attack.player",
    args = arrayOf("Steve", "Alex"),
    color = androidx.compose.ui.graphics.Color.Red
)
```

#### Rich Text Translation

Preserves colors, bold, italic, and other styling from language files:

```kotlin
import dev.aperso.composite.i18n.translateAnnotated
import dev.aperso.composite.component.Components

val annotatedString = translateAnnotated("chat.type.advancement.task", arrayOf("Player", "Advancement!"))

Components.TranslatableAnnotatedText(
    key = "chat.type.advancement.task",
    args = arrayOf("Player", "Advancement!"),
    onClick = { offset -> /* handle click */ }
)
```

Translations automatically update when the player changes their language setting.

## Requirements

*   Minecraft 26.1
*   Fabric Loader + Fabric API + Fabric Language Kotlin, **or** NeoForge

## License

All Rights Reserved.
