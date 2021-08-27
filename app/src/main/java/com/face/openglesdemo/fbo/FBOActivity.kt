package com.face.openglesdemo.fbo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.face.openglesdemo.R
import kotlinx.android.synthetic.main.activity_f_b_o.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class FBOActivity : AppCompatActivity(), FBORenderer.Callback {

    private var mRender: FBORenderer? = null
    private val mGLView: GLSurfaceView? = null

    private var mBmpWidth = 0
    private var mBmpHeight:Int = 0
    private var mImgPath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_b_o)

        gv_fbo.setEGLContextClientVersion(2)
        mRender = FBORenderer(resources)
        gv_fbo.setRenderer(mRender)
        gv_fbo.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
    }

    fun onClick(view: View?) {
        //调用相册
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val filePathColumns =
                arrayOf(MediaStore.Images.Media.DATA)
            val c =
                contentResolver.query(selectedImage!!, filePathColumns, null, null, null)
            c!!.moveToFirst()
            val columnIndex = c.getColumnIndex(filePathColumns[0])
            mImgPath = c.getString(columnIndex)
            Log.e("wuwang", "img->$mImgPath")
            val bmp = BitmapFactory.decodeFile(mImgPath)
            mBmpWidth = bmp.width
            mBmpHeight = bmp.height
            mRender?.setBitmap(bmp)
            mGLView?.requestRender()
            c.close()
        }
    }

    //图片保存
    fun saveBitmap(b: Bitmap) {
        val path: String = mImgPath.substring(0, mImgPath.lastIndexOf("/") + 1)
        val folder = File(path)
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread { Toast.makeText(this@FBOActivity, "无法保存照片", Toast.LENGTH_SHORT).show() }
            return
        }
        val dataTake = System.currentTimeMillis()
        val jpegName = "$path$dataTake.jpg"
        try {
            val fout = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        runOnUiThread {
            Toast.makeText(this@FBOActivity, "保存成功->$jpegName", Toast.LENGTH_SHORT).show()
            mImage.setImageBitmap(b)
        }
    }

    override fun onCall(data: ByteBuffer?) {
        Thread(Runnable {
            Log.e("wuwang", "callback success")
            val bitmap =
                Bitmap.createBitmap(mBmpWidth, mBmpHeight, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(data)
            saveBitmap(bitmap)
            data?.clear()
        }).start()
    }
}