package com.face.openglesdemo.util

import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20
import com.face.openglesdemo.App

object ShaderUtils {

    /**
     * 2. 创建着色器
     * vertex 顶点着色器文件目录--assets文件夹
     * fragment 片元着色器文件目录--assets文件夹
     * res 资源对象
     */
    fun createProgram(vertex : String, fragment : String, res : Resources): Int {
        //"default_vertex.sh" 创建着色器
        val mProgram = GLES20.glCreateProgram()
        //创建顶点着色器
        val vertexShader = loadShader(
            GLES20.GL_VERTEX_SHADER,
            loadFromAssetsFile(vertex,
                res) ?: ""
        )
        //创建片元着色器
        val fragmentShader = loadShader(
            GLES20.GL_FRAGMENT_SHADER,
            loadFromAssetsFile(fragment,
                res) ?: ""
        )
        //添加着色器
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        //链接着色器
        GLES20.glLinkProgram(mProgram)

        return mProgram
    }

    /**
     * 通过GLSL创建顶点或者片元着色器
     * type -- GLES20.GL_VERTEX_SHADER  顶点着色器
     *  -------GLES20.GL_FRAGMENT_SHADER 片元着色器
     */
    fun loadShader(type : Int, shaderCode : String): Int {
        //创建顶点或者片元着色器
        var shader = GLES20.glCreateShader(type)
        //将资源加入着色器
//        GLES20.glShaderSource(shader, shaderCode)
//        GLES20.glCompileShader(shader)

        if (0 != shader) {
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        //返回着色器
        return shader
    }

    /**
     * 从access文件中，读取GLSL代码字符串
     */
    fun loadFromAssetsFile(
        fname: String?,
        res: Resources
    ): String? {
        val result = StringBuilder()
        try {
            val `is` = res.assets.open(fname!!)
            var ch: Int
            val buffer = ByteArray(1024)
            while (-1 != `is`.read(buffer).also { ch = it }) {
                result.append(String(buffer, 0, ch))
            }
        } catch (e: Exception) {
            return null
        }
        return result.toString().replace("\\r\\n".toRegex(), "\n")
    }

}