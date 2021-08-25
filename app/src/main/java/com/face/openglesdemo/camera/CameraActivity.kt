package com.face.openglesdemo.camera

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.face.openglesdemo.R
import com.face.openglesdemo.util.PermissionUtils
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    //界面生命周期同步view生命周期
    override fun onResume() {
        super.onResume()
        cv_show.onResume()
    }

    override fun onPause() {
        super.onPause()
        cv_show.onPause()
    }

}