package com.example.testgl;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends Activity {

	RGLSurfaceView glview;
	private RenderGLCamera render;
	Camera camera;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		glview = (RGLSurfaceView) findViewById(R.id.glview);
		render = new RenderGLCamera(this);
		glview.setEGLContextClientVersion(2);
		glview.setRenderer(render);
		glview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		camera = Camera.open(1);

		Parameters para = camera.getParameters();

		List<Size> list = para.getSupportedPreviewSizes();

//		for (int i = 0; i < list.size(); i++) {
//			Log.e("...." + i, list.get(i).width + "*" + list.get(i).height);
//		}

		para.setPreviewSize(1280, 720);

		camera.setPreviewCallback(new PreviewCallback() {

			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {

				Log.e("onPreviewFrame", data.length + "...");
			}
		});
		try {
			camera.setDisplayOrientation(180);

			camera.setParameters(para);
			camera.startPreview();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					SurfaceTexture st = render.getTexture();
					st.setOnFrameAvailableListener(new OnFrameAvailableListener() {

						@Override
						public void onFrameAvailable(
								SurfaceTexture surfaceTexture) {
							Log.e("onFrameAvailable",
									surfaceTexture.getTimestamp() + "");
							glview.requestRender();

						}
					});

					try {
						camera.setPreviewTexture(st);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 1500);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// glview.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// glview.invalidate();
		// }
		// }, 1000);
	}

	@Override
	protected void onStop() {
		super.onStop();
		camera.stopPreview();
		camera.release();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
