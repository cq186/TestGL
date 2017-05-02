package com.example.testgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * @author JIAN
 * @date 2017-4-24 上午11:23:13
 */
public class RenderGL implements Renderer {

	Context context;
	private Bitmap bmp;
	private FloatBuffer textureBuffer; // buffer holding the texture coordinates
	private float texture[] = {
			// Mapping coordinates for the vertices
			0.0f, 1.0f, // top left (V2)
			0.0f, 0.0f, // bottom left (V1)
			1.0f, 1.0f, // top right (V4)
			1.0f, 0.0f // bottom right (V3)
	};
	private SurfaceTexture sture;

	public SurfaceTexture getTexture() {
		return sture;
	}

	private FloatBuffer vertexBuffer; // buffer holding the vertices

	private float vertices[] = { -1.0f, -1.0f, 0.0f, // V1 - bottom left
			-1.0f, 1.0f, 0.0f, // V2 - top left
			1.0f, -1.0f, 0.0f, // V3 - bottom right
			1.0f, 1.0f, 0.0f // V4 - top right
	};

	void GLBitmap() {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuffer.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
	}

	public RenderGL(Context context) {
		this.context = context;
		GLBitmap();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.e("onSurfaceCreated", "xxx");

		loadGLTexture(gl, this.context);

		gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL10.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		gl.glClearDepthf(1.0f); // Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

	}

	private int width = 0, height = 0;

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		Log.e("onSurfaceChanged", width + "*" + height);
		if (height == 0) { // Prevent A Divide By Zero By
			height = 1; // Making Height Equal One
		}
		this.width = width;
		this.height = height;
		gl.glViewport(0, 0, width, height); // Reset The
											// Current
		// Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Log.e("onDrawFrame", "onDrawFrame");
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Reset the Modelview Matrix
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -5.0f); // move 5 units INTO the screen is
											// the same as moving the camera 5
											// units away
		// square.draw(gl);
		draw(gl);
		changeGLViewport(gl);

	}

	private int frameSeq, viewportOffset, maxOffset;

	/**
	 * 通过改变gl的视角获取
	 * 
	 * @param gl
	 */
	private void changeGLViewport(GL10 gl) {
		Log.e("onDrawFrame", "changeGLViewport");
		frameSeq++;
		viewportOffset++;
		// The
		// Current
		if (frameSeq % 100 == 0) {// 每隔100帧，重置
			gl.glViewport(0, 0, width, height);
			viewportOffset = 0;
		} else {
			int k = 4;
			gl.glViewport(-maxOffset + viewportOffset * k, -maxOffset
					+ viewportOffset * k, this.width - viewportOffset * 2 * k
					+ maxOffset * 2, this.height - viewportOffset * 2 * k
					+ maxOffset * 2);
		}
	}

	/** The texture pointer */
	private int[] textures = new int[1];

	public void loadGLTexture(GL10 gl, Context context) {
		// loading texture
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);

		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Use Android GLUtils to specify a two-dimensional texture image from
		// our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		// Clean up
		bitmap.recycle();
	}

	public void draw(GL10 gl) {
		// bind the previously generated texture
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// Point to our buffers
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Set the face rotation
		gl.glFrontFace(GL10.GL_CW);

		// Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		// Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

}
