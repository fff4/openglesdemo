package com.face.openglesdemo.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square {

    private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"

    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"


    var mProgram : Int = 0

    val COORDS_PER_VERTEX = 3
    val squareCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,   // top left
        -0.5f, -0.5f, 0.0f,   // bottom left
        0.5f, -0.5f, 0.0f,   // bottom right
        0.5f,  0.5f, 0.0f  // top right
    )
    var vertexBuffer : FloatBuffer
    var drawListBuffer : ShortBuffer

    private val drawOrder =
        shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    constructor() {
        // initialize vertex byte buffer for shape coordinates

        // initialize vertex byte buffer for shape coordinates
        val bb: ByteBuffer =
            ByteBuffer.allocateDirect( // (# of coordinate values * 4 bytes per float)
                squareCoords.size * 4
            )
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        // initialize byte buffer for the draw list
        val dlb =
            ByteBuffer.allocateDirect( // (# of coordinate values * 2 bytes per short)
                drawOrder.size * 2
            )
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        //根据类型 创建顶点和片元着色器
        val vertexShader = MyGLRenderer.loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        val fragmentShader = MyGLRenderer.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private var mPositionHandle = 0
    private var mColorHandle = 0

    val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    // 设置图形的RGB值和透明度
    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    fun draw() {
        // Add program to OpenGL ES environment  使用opengl 程序
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member  获取顶点着色器的vPosition句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices 使用三角形顶点的控制句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data  设置着色器参数
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer)

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle  设置三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle GL_TRIANGLES：复用顶点的构成正方形  开始绘制
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        // 绘制图形
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);


        // Disable vertex array  禁用顶点的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

}