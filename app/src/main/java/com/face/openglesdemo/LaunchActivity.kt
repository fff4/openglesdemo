package com.face.openglesdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.face.openglesdemo.bitmap.BitmapActivit
import com.face.openglesdemo.opengl.MainActivity
import com.face.openglesdemo.rotaTranslate.RotaTranslateActivity
import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        btn_show_graphical.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btn_show_img.setOnClickListener {
            startActivity(Intent(this, BitmapActivit::class.java))
        }

        btn_show_rota_translate.setOnClickListener {
            startActivity(Intent(this, RotaTranslateActivity::class.java))
        }
    }
}