package com.face.openglesdemo.bitmap

import android.content.Context
import android.opengl.GLSurfaceView
import java.io.InputStream

class BitmapGLSurfaceView : GLSurfaceView {

    val mRenderer : BitmapRenderer

    constructor(context: Context, inputStream: InputStream?) : super(context) {
        //设置opengl的版本  2.0
        setEGLContextClientVersion(2)
        //添加渲染器
        mRenderer = BitmapRenderer(inputStream)
        setRenderer(mRenderer)
    }

}