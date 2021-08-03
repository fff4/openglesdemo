package com.face.openglesdemo.opengl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.face.openglesdemo.opengl.MyGLSurfaceView

class MainActivity : AppCompatActivity() {

    var mGLView : MyGLSurfaceView?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGLView = MyGLSurfaceView(this)
        setContentView(mGLView)
    }
}