package com.face.openglesdemo

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.face.openglesdemo.bitmap.BitmapActivit
import com.face.openglesdemo.camera.CameraActivity
import com.face.openglesdemo.opengl.MainActivity
import com.face.openglesdemo.rotaTranslate.RotaTranslateActivity
import com.face.openglesdemo.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_launch.*

/**
 * 学习 --  https://blog.csdn.net/junzia/category_6462864.html
 */
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
        btn_camera.setOnClickListener {
            PermissionUtils.askPermission(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                10,
                initViewRunnable
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode == 10,
            grantResults,
            initViewRunnable,
            Runnable {
                Toast.makeText(this, "没有获得必要的权限", Toast.LENGTH_SHORT).show()
                finish()
            })
    }

    private val initViewRunnable = Runnable {
        startActivity(Intent(this, CameraActivity::class.java))
    }
}