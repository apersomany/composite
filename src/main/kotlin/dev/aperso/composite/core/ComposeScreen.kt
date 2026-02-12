package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

open class ComposeScreen(
    title: Component = Component.empty(),
    content: @Composable () -> Unit,
): Screen(title) {
    val gui = ComposeGui(content)

    override fun init() {
        gui.init()
    }

    override fun onClose() {
        super.onClose()
        gui.onClose()
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(graphics, mouseX, mouseY, partialTick)
        gui.render(graphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (gui.mouseClicked(mouseX, mouseY, button)) {
            true
        } else {
            super.mouseClicked(mouseX, mouseY, button)
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        return if (gui.mouseReleased(mouseX, mouseY, button)) {
            true
        } else {
            super.mouseReleased(mouseX, mouseY, button)
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        return if (gui.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            true
        } else {
            super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (gui.keyPressed(keyCode, scanCode, modifiers)) {
            true
        } else {
            super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return if (gui.keyReleased(keyCode, scanCode, modifiers)) {
            true
        } else {
            super.keyReleased(keyCode, scanCode, modifiers)
        }
    }
}