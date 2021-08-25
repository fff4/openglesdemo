package com.face.openglesdemo.camera

import android.content.Context
import android.graphics.SurfaceTexture.OnFrameAvailableListener
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 相机加载view
 * GLSurfaceView 使用openes2.0来绘制界面
 * 学习位置---https://blog.csdn.net/junzia/category_6462864.html
 */
class CameraView :
    GLSurfaceView, GLSurfaceView.Renderer {

    //相机辅助类
    private var mCamera2: KitkatCamera? = null
    //opengles加载画面辅助类
    private var mCameraDrawer: CameraDrawer? = null
    private val cameraId = 0

    constructor(context:Context, attributeSet: AttributeSet) :
            super(context, attributeSet) {
        //设置opengl版本和renderer
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
        //创建类
        mCamera2 = KitkatCamera()
        mCameraDrawer = CameraDrawer(resources)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //着色器创建
        mCameraDrawer!!.onSurfaceCreated(gl, config)
        mCamera2!!.open(cameraId) //打开相机
        mCameraDrawer?.setCameraId(cameraId) //处理投影矩阵
        val point = mCamera2!!.previewSize
        mCameraDrawer?.setDataSize(point!!.x, point.y)
        //openglES创建OES纹理 创建SurfaceTexture 来直接加载相机预览图
        mCamera2!!.setPreviewTexture(mCameraDrawer?.getSurfaceTexture())
        mCameraDrawer?.getSurfaceTexture()
            ?.setOnFrameAvailableListener(OnFrameAvailableListener { requestRender() })
        //开始预览
        mCamera2!!.preview()
    }

    override fun onDrawFrame(p0: GL10?) {
        mCameraDrawer?.onDrawFrame(p0)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        mCameraDrawer?.setViewSize(width, height)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onPause() {
        super.onPause()
        mCamera2?.close()
    }

}