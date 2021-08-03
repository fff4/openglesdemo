package com.face.openglesdemo.rotaTranslate

import android.opengl.Matrix
import com.face.openglesdemo.BaseRenderer
import com.face.openglesdemo.opengl.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RotatranslateRenderer : BaseRenderer() {
    var mCube : Cube? = null

    private var tools: VaryTools? = null

    override fun onDrawFrame(p0: GL10?) {
        super.onDrawFrame(p0)
        //绘制正方体
        tools?.finalMatrix()?.let { mCube?.draw(it) }
        mCube?.draw()

        //y轴正方形平移
        tools?.pushMatrix()
        tools?.translate(0f, 3f, 0f)
        tools?.finalMatrix()?.let { mCube?.draw(it) }
        mCube?.draw()
        tools?.popMatrix()

        //y轴负方向平移，然后按xyz->(0,0,0)到(1,1,1)旋转30度
        tools?.pushMatrix()
        tools?.translate(0f, -3f, 0f)
        tools?.rotate(30f, 1f,1f,1f)
        tools?.finalMatrix()?.let { mCube?.draw(it) }
        mCube?.draw()
        tools?.popMatrix()

        //x轴负方向平移，然后按xyz->(0,0,0)到(1,-1,1)旋转120度，在放大到0.5倍
        tools?.pushMatrix()
        tools?.translate(-3f, 0f, 0f)
        tools?.rotate(120f, 1f,-1f,1f)
        tools?.scale(0.5f,0.5f,0.5f)
        tools?.finalMatrix()?.let { mCube?.draw(it) }
        mCube?.draw()
        tools?.popMatrix()

        tools?.pushMatrix()
        tools?.translate(3f,0f,0f)
        tools?.scale(1.0f,2.0f,1.0f)
        tools?.rotate(30f,1f,2f,1f)
        tools?.finalMatrix()?.let { mCube?.draw(it) }
        mCube?.draw()
        tools?.popMatrix()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(p0, width, height)

        val rate= width/height.toFloat()
        tools!!.ortho(-rate * 6, rate * 6, (-6).toFloat(),
            6f, 3f, 20f)
        tools!!.setCamera(
            0f, 0f, 10f,
            0f, 0f,  0f,
            0f, 1f, 0f)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        super.onSurfaceCreated(p0, p1)
        mCube = Cube()
        tools = VaryTools()
    }

}