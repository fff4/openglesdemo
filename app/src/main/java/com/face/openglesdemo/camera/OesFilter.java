/*
 *
 * CameraFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.face.openglesdemo.camera;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

/**
 * Description:
 */
public class OesFilter extends AFilter{

    private int mHCoordMatrix;
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);

    public OesFilter(Resources mRes) {
        super(mRes);
    }
    //设置顶点和片元着色器  --  获取颜色矩阵句柄
    @Override
    protected void onCreate() {
        createProgramByAssetsFile("oes_base_vertex.sh","oes_base_fragment.sh");
        mHCoordMatrix=GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix){
        this.mCoordMatrix=matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES20.glUniform1i(mHTexture,getTextureType());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

}
