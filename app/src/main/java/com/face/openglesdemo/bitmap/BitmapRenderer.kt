package com.face.openglesdemo.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Matrix
import com.face.openglesdemo.BaseRenderer
import java.io.InputStream
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BitmapRenderer(inputStream: InputStream?) : BaseRenderer() {

    var mGLBitmap : GLBitmap? = null
    var mGLBitmap2 : GLBitmap2? = null
    var mBitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
    var uXY : Float = 0.0f

    //重绘调用
    override fun onDrawFrame(p0: GL10?) {

//        mGLBitmap?.draw(mMVPMatrix, mBitmap)
        mGLBitmap2?.draw(mMVPMatrix, mBitmap, uXY)

    }

    //视图发生变化
    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {

        //设计变换矩阵
        val w: Int = mBitmap.getWidth()
        val h: Int = mBitmap.getHeight()
        val sWH = w / h.toFloat()
        val sWidthHeight = width / height.toFloat()
        uXY = sWidthHeight
        if(width>height){
            if(sWH>sWidthHeight){
                /**
                 * 正交投影.
                 *
                 * @param m returns the result 目标矩阵，这个数组的长度至少有16个元素，这样它才能存储正交投影矩阵；
                 * @param mOffset 结果矩阵起始的偏移量
                 * @param left x轴的最小范围
                 * @param right x轴的最大范围
                 * @param bottom y轴的最小范围
                 * @param top y轴的最大范围
                 * @param near z轴的最小范围
                 * @param far z轴的最大范围
                 */
                Matrix.orthoM(mProjectMatrix, 0,
                    -sWidthHeight*sWH,sWidthHeight*sWH, -1f,1f,
                    3f, 7f)
            }else{
                Matrix.orthoM(mProjectMatrix, 0,
                    -sWidthHeight/sWH,sWidthHeight/sWH, -1f,1f,
                    3f, 7f)
            }
        }else{
            if(sWH>sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0,
                    -1f, 1f, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,
                    3f, 7f)
            }else{
                Matrix.orthoM(mProjectMatrix, 0,
                    -1f, 1f, -sWH/sWidthHeight, sWH/sWidthHeight,
                    3f, 7f)
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0,
            0f, 0f, 7.0f,
            0f, 0f, 0f,
            0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0)
    }

    //视图被创建
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        mGLBitmap = GLBitmap()
        mGLBitmap2 = GLBitmap2()
    }

}