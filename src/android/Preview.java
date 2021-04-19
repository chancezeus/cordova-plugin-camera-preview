package com.cordovaplugincamerapreview;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import org.apache.cordova.LOG;

import java.io.IOException;
import java.util.List;

class Preview extends RelativeLayout implements SurfaceHolder.Callback {
  private final String TAG = "Preview";

  SurfaceView mSurfaceView;
  SurfaceHolder mHolder;
  Camera.Size mPreviewSize;
  List<Camera.Size> mSupportedPreviewSizes;
  Camera mCamera;
  int cameraId;
  int displayOrientation;
  int facing = Camera.CameraInfo.CAMERA_FACING_BACK;
  boolean scaleToFit = false;
  private boolean forceUpdate = false;

  Preview(Context context) {
    super(context);

    mSurfaceView = new SurfaceView(context);
    addView(mSurfaceView);

    requestLayout();

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = mSurfaceView.getHolder();
    mHolder.addCallback(this);
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void setCamera(Camera camera, int cameraId) {
    mCamera = camera;
    this.cameraId = cameraId;

    if (camera != null) {
      Camera.Parameters params = mCamera.getParameters();
      mSupportedPreviewSizes = params.getSupportedPreviewSizes();
      List<String> mFocusModes = params.getSupportedFocusModes();

      setCameraDisplayOrientation();

      if (mFocusModes.contains("continuous-picture")) {
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
      } else if (mFocusModes.contains("continuous-video")) {
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
      } else if (mFocusModes.contains("auto")) {
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
      }
      mCamera.setParameters(params);
    }
  }

  public int getDisplayOrientation() {
    return displayOrientation;
  }

  public int getCameraFacing() {
    return facing;
  }

//  public void printPreviewSize(String from) {
//    Log.d(TAG, "printPreviewSize from " + from + ": > width: " + mPreviewSize.width + " height: " + mPreviewSize.height);
//  }

//  public void setCameraPreviewSize() {
//    if (mCamera != null) {
//      Camera.Parameters parameters = mCamera.getParameters();
//      parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//      mCamera.setParameters(parameters);
//    }
//  }

  private void setCameraDisplayOrientation() {
    Camera.CameraInfo info = new Camera.CameraInfo();
    int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRotation();
    int degrees = 0;
    DisplayMetrics dm = new DisplayMetrics();

    Camera.getCameraInfo(cameraId, info);
    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

    switch (rotation) {
      case Surface.ROTATION_0:
        degrees = 0;
        break;
      case Surface.ROTATION_90:
        degrees = 90;
        break;
      case Surface.ROTATION_180:
        degrees = 180;
        break;
      case Surface.ROTATION_270:
        degrees = 270;
        break;
    }

    facing = info.facing;

    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
      displayOrientation = (info.orientation + degrees) % 360;
      displayOrientation = (360 - displayOrientation) % 360;
    } else {
      displayOrientation = (info.orientation - degrees + 360) % 360;
    }

    Log.d(TAG, "screen is rotated " + degrees + "deg from natural");
    Log.d(TAG, (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT ? "front" : "back") + " camera is oriented -" + info.orientation + "deg from natural");
    Log.d(TAG, "rotating preview " + displayOrientation + "deg");

    mCamera.setDisplayOrientation(displayOrientation);

    forceUpdate = true;
  }

  public void switchCamera(Camera camera, int cameraId) {
    try {
      setCamera(camera, cameraId);

      Log.d("CameraPreview", "before set camera");

      camera.setPreviewDisplay(mHolder);

      Log.d("CameraPreview", "before getParameters");

      Camera.Parameters parameters = camera.getParameters();

      Log.d("CameraPreview", "before setPreviewSize");

      mPreviewSize = getOptimalPreviewSize(mSurfaceView.getWidth(), mSurfaceView.getHeight());
      parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
      Log.d(TAG, mPreviewSize.width + " " + mPreviewSize.height);

      camera.setParameters(parameters);

      forceUpdate = true;
      requestLayout();
    } catch (IOException exception) {
      Log.e(TAG, exception.getMessage());
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // We purposely disregard child measurements because act as a
    // wrapper to a SurfaceView that centers the camera preview instead
    // of stretching it.
    final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    setMeasuredDimension(width, height);

    mPreviewSize = getOptimalPreviewSize(width, height);
    forceUpdate = true;
    requestLayout();
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {

    if ((forceUpdate || changed) && getChildCount() > 0) {
      final View child = getChildAt(0);

      int width = r - l;
      int height = b - t;

      int previewWidth = width;
      int previewHeight = height;

      if (mPreviewSize != null) {
        previewWidth = mPreviewSize.width;
        previewHeight = mPreviewSize.height;

        if (displayOrientation == 90 || displayOrientation == 270) {
          previewWidth = mPreviewSize.height;
          previewHeight = mPreviewSize.width;
        }

        LOG.d(TAG, "previewWidth:" + previewWidth + " previewHeight:" + previewHeight);
      }

      double widthRatio = (double) width / (double) previewWidth; // Determine ratio between available width and preview width
      double heightRatio = (double) height / (double) previewHeight; // Determine ratio between available height and preview height
      double ratio;
      if (scaleToFit) {
        ratio = Math.min(widthRatio, heightRatio);
      } else {
        ratio = Math.max(widthRatio, heightRatio);
      }

      int nW = (int) ((double) previewWidth * ratio); // Compute the new width
      int nH = (int) ((double) previewHeight * ratio); // Compute the new height
      int left = (width - nW) / 2; // Compute the left margin (will be 0 or positive on "contain", 0 or negative on "cover")
      int top = (height - nH) / 2; // Compute the top margin (will be 0 or positive on "contain", 0 or negative on "cover")

      child.layout(left, top, left + nW, top + nH);

      Log.d("layout", "left:" + left);
      Log.d("layout", "top:" + top);
      Log.d("layout", "right:" + (left + nW));
      Log.d("layout", "bottom:" + (top + nH));
    }
  }

  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, acquire the camera and tell it where
    // to draw.
    try {
      if (mCamera != null) {
        mSurfaceView.setWillNotDraw(false);
        mCamera.setPreviewDisplay(holder);
      }
    } catch (Exception exception) {
      Log.e(TAG, "Exception caused by setPreviewDisplay()", exception);
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // Surface will be destroyed when we return, so stop the preview.
    try {
      if (mCamera != null) {
        mCamera.stopPreview();
      }
    } catch (Exception exception) {
      Log.e(TAG, "Exception caused by surfaceDestroyed()", exception);
    }
  }

  private Camera.Size getOptimalPreviewSize(int w, int h) {
    if (mSupportedPreviewSizes == null || mSupportedPreviewSizes.isEmpty()) {
      return null;
    }

    final double ASPECT_TOLERANCE = 0.1;
    double targetRatio = (double) w / h;
    int targetHeight = h;
    if (displayOrientation == 90 || displayOrientation == 270) {
      targetRatio = (double) h / w;
      targetHeight = w;
    }

    Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    // Try to find an size match aspect ratio and size
    for (Camera.Size size : mSupportedPreviewSizes) {
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
      if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }

    // Cannot find the one match the aspect ratio, ignore the requirement
    if (optimalSize == null) {
      minDiff = Double.MAX_VALUE;
      for (Camera.Size size : mSupportedPreviewSizes) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize = size;
          minDiff = Math.abs(size.height - targetHeight);
        }
      }
    }

    if (optimalSize == null) {
      optimalSize = mSupportedPreviewSizes.get(0);
    }

    Log.d(TAG, "optimal preview size: w: " + optimalSize.width + " h: " + optimalSize.height);

    return optimalSize;
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    if (mCamera != null) {
      try {
        mPreviewSize = getOptimalPreviewSize(w, h);

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        //mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);
        mCamera.startPreview();

        forceUpdate = true;
        requestLayout();
      } catch (Exception exception) {
        Log.e(TAG, "Exception caused by surfaceChanged()", exception);
      }
    }
  }

//  public void setOneShotPreviewCallback(Camera.PreviewCallback callback) {
//    if (mCamera != null) {
//      mCamera.setOneShotPreviewCallback(callback);
//    }
//  }
}
