package com.face.openglesdemo.fbo

import android.content.res.Resources
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.face.openglesdemo.camera.AFilter
import com.face.openglesdemo.util.Gl2Utils
import java.nio.ByteBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FBORenderer : GLSurfaceView.Renderer {

    //离屏渲染 FBO 渲染framebuffer帧缓冲对象 数据
    var fFrame = IntArray(1)
    //渲染缓冲对象 数据
    var fRender = IntArray(1)
    //纹理 -- 需要离屏渲染 需要多一个纹理进行渲染
    var fTexture = IntArray(1)
    //创建opengles加载基类
    var fboFilter : AFilter
    //针对图片进行离屏渲染
    private var mBitmap: Bitmap? = null
    private var mBuffer: ByteBuffer? = null

    constructor(resources: Resources) : super() {
       fboFilter =  FBOFilter(resources)
    }

    fun setBitmap(bitmap: Bitmap) {
        this.mBitmap = bitmap
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        fboFilter.create()
        fboFilter.setMatrix(Gl2Utils.flip(Gl2Utils.getOriginalMatrix(), false, true))

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

    override fun onDrawFrame(p0: GL10?) {
        //判断图片是否被回收
        if (mBitmap != null && !(mBitmap?.isRecycled?:true)) {
            createEnvi()
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0])
            GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[1], 0
            )
            GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]
            )
            GLES20.glViewport(0, 0, mBitmap!!.width, mBitmap!!.height)
            fboFilter.setTextureId(fTexture[0])
            fboFilter.draw()
            GLES20.glReadPixels(
                0, 0, mBitmap!!.width, mBitmap!!.height, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, mBuffer
            )
            if (mCallback != null) {
                mCallback!!.onCall(mBuffer)
            }
            deleteEnvi()
            mBitmap!!.recycle()
        }
    }

    private fun deleteEnvi() {
        GLES20.glDeleteTextures(2, fTexture, 0)
        GLES20.glDeleteRenderbuffers(1, fRender, 0)
        GLES20.glDeleteFramebuffers(1, fFrame, 0)
    }

    fun createEnvi() {
        //获取framebuffer  renderbuffer数据
        GLES20.glGenFramebuffers(1, fFrame, 0)
        GLES20.glGenRenderbuffers(1, fRender, 0)
        //绑定renderbuffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0])
        //设置为深度的renderbuffer 并传入大小
        GLES20.glRenderbufferStorage(
            GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
            mBitmap!!.width, mBitmap!!.height
        )
        //挂载framebuffer
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER, fRender[0]
        )
        //解绑renderbuffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0)
        //获得纹理
        GLES20.glGenTextures(2, fTexture, 0)
        for (i in 0..1) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i])
            if (i == 0) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mBitmap, 0)
            } else {
                GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA,
                    mBitmap!!.width,
                    mBitmap!!.height,
                    0,
                    GLES20.GL_RGBA,
                    GLES20.GL_UNSIGNED_BYTE,
                    null
                )
            }
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
        }
        mBuffer = ByteBuffer.allocate(mBitmap!!.width * mBitmap!!.height * 4)
    }

    var mCallback: Callback? = null
    fun setCallback(callback: Callback) {
        mCallback = callback
    }
    public interface Callback {
        fun onCall(data: ByteBuffer?)
    }

}