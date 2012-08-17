package com.websiteinstantly.flashlight;

import java.io.IOException;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FlashLight extends Activity implements SurfaceHolder.Callback {
	private final static String LOG_TAG = "FlashLight";

	private ImageButton mOnBtn;
	private ImageButton mOffBtn;
	private Camera mCamera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private boolean bLightOn;
	GoogleAnalyticsTracker tracker;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mOnBtn = (ImageButton) findViewById(R.id.on_btn);
		mOnBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				processOnClick();
			}
		});
		mOffBtn = (ImageButton) findViewById(R.id.off_btn);
		mOffBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				processOffClick();
			}
		}); 
        
        // analytics 1 of 2
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.startNewSession("UA-21678350-5", this);
        tracker.trackPageView("/FlashlightMain");
        
		try {
			mCamera = Camera.open();
			mCamera.startPreview();
			Camera.Parameters params = mCamera.getParameters();
			if (params.getFlashMode() != null) {
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			}
			mCamera.setParameters(params);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Impossible d'ouvrir la camera");		
		}
		bLightOn = true;
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// analytics 2 of 2
		// Send tack info 
        tracker.dispatch();
	    // Stop the tracker when it is no longer needed.
        tracker.stopSession();
	}
	
	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		super.onPause();
	}

	private void processOffClick() {
		if (mCamera != null && bLightOn == true) {
			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(params);
			bLightOn = false;
		}
	}

	private void processOnClick() {
		if (mCamera != null && bLightOn == false) {
			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(params);
			bLightOn = true;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}
}