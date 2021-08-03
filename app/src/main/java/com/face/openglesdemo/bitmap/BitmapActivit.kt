package com.face.openglesdemo.bitmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BitmapActivit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bitmap)

        val assetManager = this.getAssets()
        val inputStream = assetManager?.open("pikagen.png");//filename是assets目录下的图片名


        val bitmapGLSurfaceView = BitmapGLSurfaceView(this, inputStream)
        setContentView(bitmapGLSurfaceView)
    }
}