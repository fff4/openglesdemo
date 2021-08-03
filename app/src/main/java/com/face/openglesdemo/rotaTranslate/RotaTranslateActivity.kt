package com.face.openglesdemo.rotaTranslate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RotaTranslateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gls = RotatranslateGLSurface(this)
        setContentView(gls)
    }
}