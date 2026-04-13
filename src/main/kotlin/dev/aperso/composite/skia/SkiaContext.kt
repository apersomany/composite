package dev.aperso.composite.skia

import org.jetbrains.skia.DirectContext
import org.lwjgl.opengl.WGL

object SkiaContext {
    private var wglContext: Long = 0

    fun initialize() {
        if (wglContext != 0L) {
            println("INFO [SkiaContext]: wglContext is already initialized. Skipping initialization.")
            return
        }
        wglContext = WGL.wglCreateContext(null, WGL.wglGetCurrentDC())
        if (wglContext == 0L) {
            return
        }
        val currentContext = WGL.wglGetCurrentContext(null)
        WGL.wglShareLists(null, currentContext, wglContext)
    }

    val directContext: DirectContext by lazy {
        DirectContext.makeGL()
    }

    fun run(runnable: Runnable) {
        val oldContext = WGL.wglGetCurrentContext(null)
        val dc = WGL.wglGetCurrentDC()

        WGL.wglMakeCurrent(null, dc, wglContext)
        try {
            runnable.run()
        } finally {
            WGL.wglMakeCurrent(null, dc, oldContext)
        }
    }
}