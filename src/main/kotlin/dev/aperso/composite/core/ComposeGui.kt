package dev.aperso.composite.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.platform.PlatformContext
import androidx.compose.ui.platform.PlatformTextInputMethodRequest
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.text.input.BackspaceCommand
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import androidx.compose.ui.text.input.CommitTextCommand
import androidx.compose.ui.text.input.EditCommand
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import dev.aperso.composite.skia.LocalSkiaSurface
import dev.aperso.composite.skia.SkiaSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import org.jetbrains.skiko.currentNanoTime
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCharCallbackI
import kotlin.math.pow

@OptIn(InternalComposeUiApi::class, ExperimentalComposeUiApi::class)
open class ComposeGui(
    val content: @Composable () -> Unit,
) : PlatformContext by PlatformContext.Empty {
    private val minecraft = Minecraft.getInstance()
    private val surface = SkiaSurface()
    private val scene = CanvasLayersComposeScene(platformContext = this)

    private val clipboard = object : Clipboard {
        override val nativeClipboard = Any()

        override suspend fun getClipEntry(): ClipEntry? {
            val text = minecraft?.keyboardHandler?.clipboard ?: return null
            return ClipEntry(StringSelection(text))
        }

        override suspend fun setClipEntry(clipEntry: ClipEntry?) {
            val transferable = clipEntry?.nativeClipEntry as? Transferable
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    val text = withContext(Dispatchers.IO) {
                        transferable.getTransferData(DataFlavor.stringFlavor)
                    } as? String
                    if (text != null) {
                        minecraft?.keyboardHandler?.clipboard = text
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private var scale = Float.NaN

    private var lastScrollTime = currentNanoTime()
    private var scrollX = 0f
    private var scrollY = 0f

    init {
        scene.setContent {
            CompositionLocalProvider(
                LocalSkiaSurface provides surface,
                LocalClipboard provides clipboard
            ) {
                content()
            }
        }
    }

    private var onEditCommand: ((List<EditCommand>) -> Unit)? = null

    override suspend fun startInputMethod(request: PlatformTextInputMethodRequest): Nothing {
        try {
            onEditCommand = request.onEditCommand
            awaitCancellation()
        } finally {
            onEditCommand = null
        }
    }

    override fun setPointerIcon(pointerIcon: PointerIcon) {
        val cursor = when (pointerIcon) {
            PointerIcon.Hand -> GLFW.GLFW_HAND_CURSOR
            PointerIcon.Text -> GLFW.GLFW_IBEAM_CURSOR
            PointerIcon.Crosshair -> GLFW.GLFW_CROSSHAIR_CURSOR
            else -> GLFW.GLFW_ARROW_CURSOR
        }
        minecraft?.window?.let {
            GLFW.glfwSetCursor(
                it.window,
                GLFW.glfwCreateStandardCursor(cursor)
            )
        }
    }

    private var charCallback: GLFWCharCallbackI? = null

    open fun init() {
        val window = minecraft.window
        surface.resize(window.width, window.height)
        scale = window.guiScale.toFloat()
        scene.size = IntSize(window.width, window.height)
        scene.density = Density(scale * 0.5f, 1.0f)
        if (charCallback == null) {
            charCallback = GLFW.glfwSetCharCallback(minecraft.window.window) {
                _, codepoint -> onEditCommand?.invoke(listOf(CommitTextCommand(Char(codepoint).toString(), 1)))
            }
        }
    }

    open fun onClose() {
        scene.close()
        GLFW.glfwSetCharCallback(minecraft.window.window, charCallback)
    }

    open fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        scene.sendPointerEvent(
            PointerEventType.Move,
            Offset(mouseX * scale, mouseY * scale)
        )
        val currentTime = currentNanoTime()
        val deltaT = (currentTime - lastScrollTime).shr(16) * 0.001f
        lastScrollTime = currentTime
        val decayX = scrollX - scrollX * 0.3f.pow(deltaT)
        val decayY = scrollY - scrollY * 0.3f.pow(deltaT)
        scene.sendPointerEvent(
            PointerEventType.Scroll,
            Offset(mouseX * scale, mouseY * scale),
            Offset(decayX * scale, decayY * scale)
        )
        scrollX -= decayX
        scrollY -= decayY
        surface.render(guiGraphics) {
            scene.render(it, currentTime)
        }
    }

    open fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        scene.sendPointerEvent(
            PointerEventType.Press,
            Offset((mouseX * scale).toFloat(), (mouseY * scale).toFloat()),
            button = PointerButton(button)
        )
        return true
    }

    open fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        scene.sendPointerEvent(
            PointerEventType.Release,
            Offset((mouseX * scale).toFloat(), (mouseY * scale).toFloat()),
            button = PointerButton(button)
        )
        return true
    }

    open fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scrollX += scrollX.toFloat()
        this.scrollY -= scrollY.toFloat()
        return true // Returning true as gui handled it, though original called super.
    }

    private fun keyEvent(type: KeyEventType, keyCode: Int, modifiers: Int): KeyEvent {
        return KeyEvent(
            Key(
                when (keyCode) {
                    GLFW.GLFW_KEY_UP -> java.awt.event.KeyEvent.VK_UP
                    GLFW.GLFW_KEY_LEFT -> java.awt.event.KeyEvent.VK_LEFT
                    GLFW.GLFW_KEY_DOWN -> java.awt.event.KeyEvent.VK_DOWN
                    GLFW.GLFW_KEY_RIGHT -> java.awt.event.KeyEvent.VK_RIGHT
                    else -> keyCode
                }
            ),
            type,
            isCtrlPressed = (modifiers and GLFW.GLFW_MOD_CONTROL) != 0,
            isMetaPressed = (modifiers and GLFW.GLFW_MOD_SUPER) != 0,
            isAltPressed = (modifiers and GLFW.GLFW_MOD_ALT) != 0,
            isShiftPressed = (modifiers and GLFW.GLFW_MOD_SHIFT) != 0
        )
    }

    open fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val result = scene.sendKeyEvent(keyEvent(KeyEventType.KeyDown, keyCode, modifiers))
        return if (result) {
            true
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            onEditCommand?.invoke(listOf(BackspaceCommand()))
            true
        } else {
            false
        }
    }

    open fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return scene.sendKeyEvent(keyEvent(KeyEventType.KeyUp, keyCode, modifiers))
    }
}
