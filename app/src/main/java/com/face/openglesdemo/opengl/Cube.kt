package com.face.openglesdemo.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * 绘制一个正方体
 */
class Cube {

    //创建顶点 --  世界坐标的8个点
    val cube_positions = floatArrayOf(
        -1.0f,1.0f,1.0f,    //正面左上0
        -1.0f,-1.0f,1.0f,   //正面左下1
        1.0f,-1.0f,1.0f,    //正面右下2
        1.0f,1.0f,1.0f,     //正面右上3
        -1.0f,1.0f,-1.0f,    //反面左上4
        -1.0f,-1.0f,-1.0f,   //反面左下5
        1.0f,-1.0f,-1.0f,    //反面右下6
        1.0f,1.0f,-1.0f     //反面右上7
    )

    //配置三角拆分
    val index = shortArrayOf(
        6,7,4,6,4,5,    //后面
        6,3,7,6,2,3,    //右面
        6,5,1,6,1,2,    //下面
        0,3,2,0,2,1,    //正面
        0,1,5,0,5,4,    //左面
        0,7,3,0,4,7    //上面
    )

    //颜色值
    val color = floatArrayOf(
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        0f,1f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f,
        1f,0f,0f,1f
    )

    //创建着色器GLSL  顶点着色器
    val vertexShaderCode =
                "attribute vec4 vPosition;" +
                "uniform mat4 uMVPMatrix;"+
                "varying  vec4 vColor;"+
                "attribute vec4 aColor;"+
                "void main() {" +
                "  gl_Position = uMVPMatrix*vPosition;" +
                "  vColor=aColor;"+
                "}"
    //片元着色器
    val fragmentShaderCode = "precision mediump float;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

    val vertexBuffer : FloatBuffer
    val colorBuffer : FloatBuffer
    val indexBuffer : ShortBuffer
    val program : Int

    constructor() {
        //1、创建缓存空间，将顶点放到gl
        val cubeBuffer = ByteBuffer.allocateDirect(cube_positions.size * 4)
        cubeBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = cubeBuffer.asFloatBuffer()
        vertexBuffer.put(cube_positions)
        vertexBuffer.position(0)

        val dd = ByteBuffer.allocateDirect(
                color.size * 4)
        dd.order(ByteOrder.nativeOrder())
        colorBuffer = dd.asFloatBuffer()
        colorBuffer.put(color)
        colorBuffer.position(0)

        val cc= ByteBuffer.allocateDirect(index.size*2);
        cc.order(ByteOrder.nativeOrder())
        indexBuffer=cc.asShortBuffer()
        indexBuffer.put(index)
        indexBuffer.position(0)


        val vertexShader = MyGLRenderer.loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        val fragmentShader = MyGLRenderer.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )
        //2.创建着色器
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    var mPositionHandle : Int = 0
    var mColorHandle : Int = 0
    var mMatrixHandler : Int = 0
    val COORDS_PER_VERTEX = 3
    val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        //3.绘制图形
        //将程序加入gl2.0
        GLES20.glUseProgram(program)
        //获得句柄
        mPositionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        //启动句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        //准备坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,false, 0, vertexBuffer
            )
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetAttribLocation(program, "aColor");
        //设置颜色
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,
            GLES20.GL_FLOAT,false,
            0,colorBuffer)
        //索引法绘制正方体
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.size,
            GLES20.GL_UNSIGNED_SHORT,indexBuffer)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    fun draw(mvpMatrix : FloatArray) {

        //获取变换矩阵句柄
        mMatrixHandler = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0)
        //绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cube_positions.size/COORDS_PER_VERTEX)
        // Disable vertex array// 允许使用顶点颜色数组
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

}