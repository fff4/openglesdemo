package com.face.openglesdemo.fbo

import android.content.res.Resources
import com.face.openglesdemo.camera.AFilter

class FBOFilter(res:Resources) : AFilter(res) {
    override fun onCreate() {
        createProgramByAssetsFile(
            "base_vertex.sh",
            "gray_fragment.frag"
        )
    }

    override fun onSizeChanged(width: Int, height: Int) {

    }

}