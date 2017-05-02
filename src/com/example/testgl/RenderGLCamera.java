package com.example.testgl;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

/**
 * @author JIAN
 * @date 2017-4-24 上午11:23:13
 */
public class RenderGLCamera implements Renderer {

	Context context;

	private SurfaceTexture sture;

	public SurfaceTexture getTexture() {
		return sture;
	}

	private int createTextureID() {
		int[] texture = new int[1];

		GLES20.glGenTextures(1, texture, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		return texture[0];
	}

	public RenderGLCamera(Context context) {
		this.context = context;
	}

	private int mTextureID;

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.e("onSurfaceCreated", "xxx");

		mTextureID = createTextureID();
		sture = new SurfaceTexture(mTextureID);
		mDirectDrawer = new DirectDrawer(mTextureID);
		Log.e("onSurfaceCreated", "xxx。。。。" + mTextureID);

	}

	private int width = 0, height = 0;

	DirectDrawer mDirectDrawer;

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		Log.e("onSurfaceChanged", width + "*" + height);

	}
	float[] mtx = new float[16];
	@Override
	public void onDrawFrame(GL10 gl) {
		// Log.e("onDrawFrame", "onDrawFrame");
		sture.updateTexImage();
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glViewport(100, 200, 288, 512);
		
//		[0.0, -1.0, 0.0, 0.0,
//		1.0, 0.0, 0.0, 0.0,
//		0.0, 0.0, 1.0, 0.0,
//		0.0, 1.0, 0.0, 1.0]
		
		sture.getTransformMatrix(mtx);
		Log.e("onDrawFrame", Arrays.toString(mtx));
		mDirectDrawer.draw(mtx);

	}

}
