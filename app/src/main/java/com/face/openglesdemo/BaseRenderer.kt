package com.face.openglesdemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class BaseRenderer : GLSurfaceView.Renderer {

    // 定义矩形变换的存储对象
    val mMVPMatrix = FloatArray(16)
    val mProjectMatrix = FloatArray(16)
    val mViewMatrix = FloatArray(16)

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)//设置视窗尺寸
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f,0f,0f,1.0f) //设置一个清空屏幕的颜色
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

}