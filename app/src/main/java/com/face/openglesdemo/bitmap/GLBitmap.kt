package com.face.openglesdemo.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import com.face.openglesdemo.App
import com.face.openglesdemo.util.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 绘制一张图片  1.绘制一个矩形  2.将图片通过纹理加载到矩形上
 * 利用纹理图
 */
class GLBitmap {
    //设置顶点坐标
    val bitmap_position = floatArrayOf(
        -1.0f,1.0f,    //左上角
        -1.0f,-1.0f,   //左下角
        1.0f,1.0f,     //右上角
        1.0f,-1.0f     //右下角
    )
    //设置纹理坐标
    private val sCoord = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
    )
    var color = floatArrayOf(0.9f,0.9f,0.9f,1.0f) //白色
    val vertexBuffer : FloatBuffer
    val coordBuffer : FloatBuffer
    val mProgram : Int

    constructor() {
        //1. 申请系统存储空间
        val positionBuffer = ByteBuffer.allocateDirect(bitmap_position.size * 4)
        positionBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = positionBuffer.asFloatBuffer()
        vertexBuffer.put(bitmap_position)
        vertexBuffer.position(0)

        val coordBuffers = ByteBuffer.allocateDirect(sCoord.size * 4)
        coordBuffers.order(ByteOrder.nativeOrder())
        coordBuffer = coordBuffers.asFloatBuffer()
        coordBuffer.put(sCoord)
        coordBuffer.position(0)

        //2.创建着色器
        mProgram = ShaderUtils.createProgram("default_vertex.sh",
            "default_fragment.sh", App.mContext.resources)
    }

    var mMatrixHandler : Int = 0
    private var mPositionHandle = 0
    private var maCoordinateHandle = 0
    private var mvTextureHandle = 0
    val COORDS_PER_VERTEX = 3
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    var mBitmap : Bitmap? = null

    fun draw(mvpMatrix : FloatArray, bitmap : Bitmap) {
        //3、绘制
        mBitmap = bitmap
        //获取变换矩阵句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0)

        // 将程序加入到OpenGL2.0环境
        GLES20.glUseProgram(mProgram)
        // get handle to vertex shader's vPosition member  获取顶点着色器的vPosition句柄 根据不同变量类型获取对应的值
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        // 获取片元着色器vColor成员的句柄
        maCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate")
        GLES20.glEnableVertexAttribArray(maCoordinateHandle)

        mvTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")
        GLES20.glUniform1i(mvTextureHandle, 0)

        val textureId=createTexture()
        //传入顶点坐标
        GLES20.glVertexAttribPointer(mPositionHandle,2,
            GLES20.GL_FLOAT,false,
            0,vertexBuffer)
        //传入纹理坐标
        GLES20.glVertexAttribPointer(maCoordinateHandle,2,
            GLES20.GL_FLOAT,false,0,coordBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)
    }

    /**
     * 将图片转化成纹理数据
     */
    private fun createTexture(): Int {
        val texture = IntArray(1)
        if (mBitmap != null && !mBitmap?.isRecycled()!!) {
            //生成纹理
            GLES20.glGenTextures(1, texture, 0)
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST.toFloat()
            )
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR.toFloat()
            )
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
            return texture[0]
        }
        return 0
    }

}