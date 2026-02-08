package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

open class ComposeScreen(
    title: Component = Component.empty(),
    content: @Composable () -> Unit,
): Screen(title) {
    val core = ComposeScreenCore(content)

    override fun init() {
        core.init()
    }

    override fun onClose() {
        super.onClose()
        core.onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        core.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return core.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return core.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        core.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (core.keyPressed(keyCode, scanCode, modifiers)) {
            true
        } else {
            super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (core.keyReleased(keyCode, scanCode, modifiers)) {
            true
        } else {
            super.keyReleased(keyCode, scanCode, modifiers)
        }
    }
}