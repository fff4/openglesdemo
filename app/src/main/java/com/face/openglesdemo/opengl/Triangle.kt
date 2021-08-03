package com.face.openglesdemo.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 绘制等腰三角形
 */
class Triangle {

    /*private val vertexShaderCode = "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "}"*/
    //相比上面，添加了矩阵变量
    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +  // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    // Use to access and set the view transformation
    private var mMVPMatrixHandle = 0

    private val fragmentShaderCode = "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

    var mProgram : Int = 0

    val COORDS_PER_VERTEX = 3

    val vertexBuffer : FloatBuffer

    val triangleCoords: FloatArray = floatArrayOf(
        0.0f,  0.622008459f, 0.0f, // top
        -0.5f, -0.311004243f, 0.0f, // bottom left
        0.5f, -0.311004243f, 0.0f // bottom right
    )

    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    constructor() {
        //分配缓冲区 -- 将顶点坐标传入OpenGL
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        //设置字节序   ByteOrder nativeOrder() 返回当前硬件平台的字节序。
        bb.order(ByteOrder.nativeOrder())
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer()

        vertexBuffer.put(triangleCoords)

        vertexBuffer.position(0)

        //根据类型 创建顶点和片元着色器
        val vertexShader = MyGLRenderer.loadShader(
            GLES20.GL_VERTEX_SHADER,
            vertexShaderCode
        )
        val fragmentShader = MyGLRenderer.loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode
        )

        // 创建一个空的opengl程序
        mProgram = GLES20.glCreateProgram();

        // 将顶点着色器加入程序
        GLES20.glAttachShader(mProgram, vertexShader);

        // 将片元着色器加入程序
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 连接到着色器程序
        GLES20.glLinkProgram(mProgram);

    }

    private var mPositionHandle = 0
    private var mColorHandle = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex


    fun draw() {
        // 将程序加入到OpenGL2.0环境
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member  获取顶点着色器的vPosition句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer)

        // 获取片元着色器vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 设置三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array  禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    fun draw(mvpMatrix : FloatArray) {
        //获取变换矩阵uMVPMatrix成员句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        // 指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        // 图形绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array// 允许使用顶点颜色数组
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

}