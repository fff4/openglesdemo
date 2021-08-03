package com.face.openglesdemo.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 绘制一个球
 *  球的计算方式
 *  角a  ：  原点到点的连线  与z轴的夹角
 *  角b  ：  原点到点的连线，在xy轴的投影，与x的夹角
 *
 *  x=rsinθcosφ.
 *  y=rsinθsinφ.
 *  z=rcosθ.
 */
class Ball {

    fun getCone_position(height:Float): FloatArray {
        var data = mutableListOf<Float>()
        var r1: Float
        var r2: Float
        var h1: Float
        var h2: Float
        var sin: Float
        var cos: Float
        var step: Float = 5f
        for (i in 0..37) { //以经纬度为标准 --- -90到90是经度，角a
            val j = -90 + i * step
            r1 = Math.cos(i * Math.PI / 180.0).toFloat()
            r2 = Math.cos((i + step) * Math.PI / 180.0).toFloat()
            h1 = Math.sin(i * Math.PI / 180.0).toFloat()
            h2 = Math.sin((i + step) * Math.PI / 180.0).toFloat()
            // 固定纬度, 360 度旋转遍历一条纬线
            val step2 = step * 2
            for (i in 0..361) {  // 以经度点  绘制维度圈
                val j = i * step2
                cos = Math.cos(j * Math.PI / 180.0).toFloat()
                sin = (-Math.sin(j * Math.PI / 180.0)).toFloat()
                //添加坐标点
                data.add(r2 * cos)
                data.add(h2)
                data.add(r2 * sin)
                data.add(r1 * cos)
                data.add(h1)
                data.add(r1 * sin)
            }
        }

        val f = FloatArray(data.size)
        for (i in f.indices) {
            f[i] = data[i]
        }

        return f
    }

    //定义顶点
    var cone_position: FloatArray

    private val vertexShaderCode =
        "varying vec4 vColor;" +
        "attribute vec4 vPosition;" +
                "uniform mat4 uMVPMatrix;" +
                "void main() {" +
                "gl_Position = uMVPMatrix * vPosition;" +
                "float color;" +
                " if(vPosition.z>0.0){" +
                " color=vPosition.z;" +
                "    }else{" +
                " color=-vPosition.z;" +
                "    }" +
                "vColor=vec4(color,color,color,1.0);" +
                "}"
    //片元着色器
    val fragmentShaderCode = "precision mediump float;" +
            "varying vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}"

    var color = floatArrayOf(0.9f,0.9f,0.9f,1.0f) //白色
    val vertexBuffer : FloatBuffer
    val mProgram : Int

    constructor(height: Float) {
        cone_position = getCone_position(height)
        //分配缓冲区 -- 将顶点坐标传入OpenGL
        val bb = ByteBuffer.allocateDirect(cone_position.size * 4)
        //设置字节序   ByteOrder nativeOrder() 返回当前硬件平台的字节序。
        bb.order(ByteOrder.nativeOrder())
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(cone_position)
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
        GLES20.glAttachShader(mProgram, vertexShader)
        // 将片元着色器加入程序
        GLES20.glAttachShader(mProgram, fragmentShader)
        // 连接到着色器程序
        GLES20.glLinkProgram(mProgram)
    }


    private var mPositionHandle = 0
    private var mColorHandle = 0
    val COORDS_PER_VERTEX = 3
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw() {
        // 将程序加入到OpenGL2.0环境
        GLES20.glUseProgram(mProgram)
        // get handle to vertex shader's vPosition member  获取顶点着色器的vPosition句柄 根据不同变量类型获取对应的值
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        // 准备三角形的坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer)

        // 获取片元着色器vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        // 设置三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, cone_position.size/COORDS_PER_VERTEX)

        // Disable vertex array  禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    var mMatrixHandler : Int = 0

    fun draw(mvpMatrix : FloatArray) {

        //获取变换矩阵句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        //指定vMatrix的值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mvpMatrix, 0)
        //绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, cone_position.size/COORDS_PER_VERTEX)
        // Disable vertex array// 允许使用顶点颜色数组
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }
}