package com.face.openglesdemo.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {

    var mTriangle : Triangle? = null
    var mSqura : Square? = null
    var mCircular : Circular? = null
    var mCircular2 : Circular? = null
    var mCube : Cube? = null
    var mCone : Cone? = null
    var mCylinder : Cylinder? = null
    var mBall : Ball? = null

    // 定义矩形变换的存储对象
    val mMVPMatrix = FloatArray(16)
    val mProjectMatrix = FloatArray(16)
    val mViewMatrix = FloatArray(16)

    //view 重绘调用
    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        // Draw shape
//        mTriangle?.draw(mMVPMatrix)
//        mTriangle?.draw()

//        mCircular?.draw(mMVPMatrix)
//        mCircular?.draw()
//
//        mCircular2?.draw(mMVPMatrix)
//        mCircular2?.draw()
//        mSqura?.draw()

        //绘制正方体
//        mCube?.draw(mMVPMatrix)
//        mCube?.draw()
        //绘制圆锥----绘制锥面  +  绘制圆 == 圆锥
//        mCone?.draw(mMVPMatrix)
//        mCone?.draw()
        //圆柱面-----相当于圆锥面的双倍
//        mCylinder?.draw(mMVPMatrix)
//        mCylinder?.draw()

        mBall?.draw(mMVPMatrix)
        mBall?.draw()

    }

    //视图发生变化  （例如，当设备的屏幕方向改变时）
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)//设置视窗尺寸

       /* //计算宽高比  平视
        val ratio = width.toFloat() / height
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0,
            -ratio, ratio, -1f, 1f,
            3f, 7f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, //变换矩阵的起始位置
            0f, 0f, 7.0f,//相机的起始位置
            0f, 0f, 0f,//观测点的位置
            0f, 1.0f, 0.0f)//up向量在xyz上的分量
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)*/

        //正方体
        //计算宽高比
        val ratio= width/height.toFloat()
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0,
            -ratio, ratio,
            -1f, 1f,
            3f, 20f);
        //设置相机位置----正方体
      /*  Matrix.setLookAtM(mViewMatrix, 0,
            5.0f, 5.0f, 10.0f,
            0f, 0f, 0f,
            0f, 2.0f, 0.0f);*/
        //设置相机位置----圆锥
        //设置相机位置
        Matrix.setLookAtM(
            mViewMatrix,
            0,
            1.0f, 10.0f, -4.0f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f
        )

        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)

        mCone = Cone(height.toFloat())
        mCylinder = Cylinder(height.toFloat())
        mBall = Ball(height.toFloat())

    }

    //opengl环境被创建 被创建
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0f,0f,0f,1.0f) //设置一个清空屏幕的颜色
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        //初始化三角形
        mTriangle = Triangle()
//        mSqura = Square()
        mCircular = Circular()
        mCircular2 = Circular()
        mCube = Cube()


    }

    companion object {

        fun loadShader(type : Int, shaderCode : String): Int {
            //创建顶点或者片元着色器
            val shader = GLES20.glCreateShader(type)
            //将资源加入着色器
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            //返回着色器
            return shader

        }
    }

}