package com.face.openglesdemo.camera

import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import java.io.IOException
import java.util.*

public class KitkatCamera : ICamera {

    private var mConfig: ICamera.Config? = null
    private var mCamera: Camera? = null
    private var sizeComparator: CameraSizeComparator? = null

    private var picSize: Camera.Size? = null
    private var preSize: Camera.Size? = null

    private var mPicSize: Point? = null
    private var mPreSize: Point? = null

    init {
        mConfig = ICamera.Config()
        mConfig?.minPreviewWidth = 720
        mConfig?.minPictureWidth = 720
        mConfig?.rate = 1.778f
        sizeComparator = CameraSizeComparator()
    }

    override fun open(cameraId: Int): Boolean {
        mCamera = Camera.open(cameraId)

        if (mCamera != null) {
            val parameters = mCamera!!.parameters
            picSize = mConfig?.rate?.let {
                mConfig?.minPictureWidth?.let { it1 ->
                    getPropPictureSize(parameters.supportedPictureSizes,
                        it, it1
                    )
                }
            }
            preSize =mConfig?.rate?.let {
                mConfig?.minPictureWidth?.let { it1 ->
                    getPropPreviewSize(parameters.supportedPictureSizes,
                        it, it1
                    )
                }
            }
            parameters.setPictureSize(picSize!!.width, picSize!!.height)
            parameters.setPreviewSize(preSize!!.width, preSize!!.height)
            mCamera!!.parameters = parameters
            val pre: Camera.Size = parameters.getPreviewSize()
            val pic: Camera.Size = parameters.getPictureSize()
            mPicSize = Point(pic.height, pic.width)
            mPreSize = Point(pre.height, pre.width)
            Log.e("open", "camera previewSize:" + mPreSize!!.x +
                    "/" + mPreSize!!.y)
            return true
        }
        return false
    }

    override fun setConfig(config: ICamera.Config?) {
        this.mConfig = config
    }

    override fun setPreviewTexture(texture: SurfaceTexture?) {
        if (mCamera != null) {
            try {
                mCamera!!.setPreviewTexture(texture)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun preview(): Boolean {
        if (mCamera != null) {
            mCamera?.startPreview()
        }
        return false
    }

    override fun switchTo(cameraId: Int): Boolean {
        close()
        open(cameraId)
        return false
    }

    override fun takePhoto(callback: ICamera.TakePhotoCallback?) {}
    //关闭相机预览
    override fun close(): Boolean {
        if (mCamera != null) {
            try {
                mCamera!!.stopPreview()
                mCamera!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun getPreviewSize(): Point? {
        return mPreSize
    }

    override fun getPictureSize(): Point? {
        return mPicSize
    }

    override fun setOnPreviewFrameCallback(callback: ICamera.PreviewFrameCallback) {
        if (mCamera != null) {
            mCamera!!.setPreviewCallback { data, camera ->
                callback.onPreviewFrame(
                    data,
                    mPreSize!!.x,
                    mPreSize!!.y
                )
            }
        }
    }

    private fun getPropPreviewSize(
        list: List<Camera.Size>,
        th: Float,
        minWidth: Int
    ): Camera.Size? {
        Collections.sort(list, sizeComparator)
        var i = 0
        for (s in list) {
            if (s.height >= minWidth && equalRate(s, th)) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0
        }
        return list[i]
    }

    private fun getPropPictureSize(
        list: List<Camera.Size>,
        th: Float,
        minWidth: Int
    ): Camera.Size? {
        Collections.sort(list, sizeComparator)
        var i = 0
        for (s in list) {
            if (s.height >= minWidth && equalRate(s, th)) {
                break
            }
            i++
        }
        if (i == list.size) {
            i = 0
        }
        return list[i]
    }

    private fun equalRate(
        s: Camera.Size,
        rate: Float
    ): Boolean {
        val r = s.width.toFloat() / s.height.toFloat()
        return if (Math.abs(r - rate) <= 0.03) {
            true
        } else {
            false
        }
    }

    private class CameraSizeComparator :
        Comparator<Camera.Size> {
        override fun compare(
            lhs: Camera.Size,
            rhs: Camera.Size
        ): Int {
            // TODO Auto-generated method stub
            return if (lhs.height == rhs.height) {
                0
            } else if (lhs.height > rhs.height) {
                1
            } else {
                -1
            }
        }
    }

}