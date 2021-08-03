package com.face.openglesdemo.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView

class MyGLSurfaceView : GLSurfaceView {

    var mMyGLRenderer : MyGLRenderer? = null

    constructor(context: Context) : super(context) {
        //设置OpenGL版本是2.0  -----   1.0  2.0  3.0
        setEGLContextClientVersion(2)

        mMyGLRenderer = MyGLRenderer()
        //设置渲染器
        setRenderer(mMyGLRenderer)
    }

}