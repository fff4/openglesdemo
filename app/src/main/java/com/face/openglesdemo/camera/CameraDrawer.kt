package com.face.openglesdemo.camera

import android.content.res.Resources
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.face.openglesdemo.util.Gl2Utils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraDrawer(resources: Resources) : GLSurfaceView.Renderer {
    private var surfaceTexture: SurfaceTexture? = null
    private var width = 0
    private var height:Int = 0
    private var dataWidth = 0
    private var dataHeight:Int = 0
    private val mOesFilter: AFilter = OesFilter(resources)
    private var cameraId = 0
    private val matrix = FloatArray(16)

    fun setDataSize(dataWidth: Int, dataHeight: Int) {
        this.dataWidth = dataWidth
        this.dataHeight = dataHeight
        calculateMatrix()
    }
    fun setViewSize(width: Int, height: Int) {
        this.width = width
        this.height = height
        calculateMatrix()
    }

    //对投影矩阵进行旋转-镜像操作，达到画面旋转的效果
    //根据相机id对矩阵进行旋转镜像操作
    private fun calculateMatrix() {
        Gl2Utils.getShowMatrix(matrix, dataWidth, dataHeight, width, height)
        if (cameraId == 1) {
            Gl2Utils.flip(matrix, true, false)
            Gl2Utils.rotate(matrix, 90f)
        } else {
            //镜像操作 --- x轴点-1缩放
            Gl2Utils.flip(matrix, true, false)
            Gl2Utils.rotate(matrix, 90f)
        }
        mOesFilter.matrix = matrix
    }

    fun getSurfaceTexture(): SurfaceTexture? {
        return surfaceTexture
    }

    fun setCameraId(id: Int) {
        cameraId = id
        calculateMatrix()
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        val texture: Int = createTextureID()
        surfaceTexture = SurfaceTexture(texture)
        mOesFilter.create()
        mOesFilter.textureId = texture
    }

    override fun onDrawFrame(p0: GL10?) {
        if (surfaceTexture != null) {
            surfaceTexture!!.updateTexImage()
        }
        mOesFilter.draw()
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        setViewSize(width, height)
    }

    //创建纹理id
    private fun createTextureID(): Int {
        val texture = IntArray(1)
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0])
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE
        )
        return texture[0]
    }

}