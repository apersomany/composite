package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
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

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick)
        gui.extractRenderState(graphics, mouseX, mouseY, partialTick)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        return if (gui.mouseClicked(event.x(), event.y(), event.button())) {
            true
        } else {
            super.mouseClicked(event, doubleClick)
        }
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        return if (gui.mouseReleased(event.x(), event.y(), event.button())) {
            true
        } else {
            super.mouseReleased(event)
        }
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        return if (gui.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            true
        } else {
            super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
        }
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        return if (gui.keyPressed(event.key(), event.scancode(), event.modifiers())) {
            true
        } else {
            super.keyPressed(event)
        }
    }

    override fun keyReleased(event: KeyEvent): Boolean {
        return if (gui.keyReleased(event.key(), event.scancode(), event.modifiers())) {
            true
        } else {
            super.keyReleased(event)
        }
    }
}