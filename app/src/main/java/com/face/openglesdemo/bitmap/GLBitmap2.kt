package com.face.openglesdemo.bitmap

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
 * 利用纹理图 --  对图片进行黑白 暖色  模糊等处理
 */
class GLBitmap2 {

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

        //2.创建着色器  TODO 不显示问题，片元着色器GLSL的变量值没有赋值（顶点坐标和矩阵变换后的坐标没有传递过来）
        mProgram = ShaderUtils.createProgram("default_vertex.sh",
            "half_color_fragment.sh", App.mContext.resources)

    }

    var mMatrixHandler : Int = 0
    private var mPositionHandle = 0
    private var maCoordinateHandle = 0
    private var mvTextureHandle = 0
    val COORDS_PER_VERTEX = 3
    var mBitmap : Bitmap? = null

    fun draw(mvpMatrix: FloatArray, bitmap: Bitmap, uXY: Float) {
        mBitmap = bitmap

        val hChangeType = GLES20.glGetUniformLocation(mProgram, "vChangeType")
        val hChangeColor = GLES20.glGetUniformLocation(mProgram, "vChangeColor")
        val hIsHalf = GLES20.glGetUniformLocation(mProgram, "vIsHalf")
        val glHUxy = GLES20.glGetUniformLocation(mProgram, "uXY")
        //获取变换矩阵句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        // get handle to vertex shader's vPosition member  获取顶点着色器的vPosition句柄 根据不同变量类型获取对应的值
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 获取片元着色器vColor成员的句柄
        maCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vCoordinate")
        mvTextureHandle = GLES20.glGetUniformLocation(mProgram, "vTexture")

        // 将程序加入到OpenGL2.0环境
        GLES20.glUseProgram(mProgram)
        //给变量赋值
        GLES20.glUniform1i(hChangeType, Filter.MAGN.type)
        GLES20.glUniform3fv(hChangeColor, 1, Filter.MAGN.data(), 0)
        GLES20.glUniform1i(hIsHalf, if (false) 1 else 0)
        GLES20.glUniform1f(glHUxy, uXY)

        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0)
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glEnableVertexAttribArray(maCoordinateHandle)
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


    enum class Filter(val type: Int, private val data: FloatArray) {
        NONE(0, floatArrayOf(0.0f, 0.0f, 0.0f)),
        //黑白图片
        GRAY(1, floatArrayOf(0.299f, 0.587f, 0.114f)),
        //简单色彩处理，冷暖色调、增加亮度、降低亮度等
        COOL(2, floatArrayOf(0.0f, 0.0f, 0.1f)),
        WARM(2, floatArrayOf(0.1f, 0.1f, 0.0f)),
        //模糊处理
        BLUR(3, floatArrayOf(0.006f, 0.004f, 0.002f)),
        //放大镜效果
        MAGN(4, floatArrayOf(0.0f, 0.0f, 0.4f));

        fun data(): FloatArray {
            return data
        }

    }

}