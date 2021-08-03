package com.face.openglesdemo.rotaTranslate

import android.content.Context
import android.opengl.GLSurfaceView

class RotatranslateGLSurface(context: Context) : GLSurfaceView(context) {

    init {
        setEGLContextClientVersion(2)
        setRenderer(RotatranslateRenderer())
    }

}