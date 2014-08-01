package com.kpj.timelachs.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.kpj.timelachs.R;

import java.io.IOException;
import java.util.List;

class Preview extends ViewGroup implements SurfaceHolder.Callback {
    Camera cam;

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;

    Camera.Size pSize;
    Camera.Size iSize;

    Preview(Context ctx) {
        super(ctx);

        mSurfaceView = (SurfaceView) ((Activity) ctx).findViewById(R.id.cam_preview);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

    }

    public void setCamera(Camera camera) {
        if (cam == camera) { return; }

        stopPreviewAndFreeCamera();

        cam = camera;

        if (cam != null) {
            requestLayout();

            cam.startPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (cam != null) {
            cam.stopPreview();
        }
    }

    private void stopPreviewAndFreeCamera() {

        if (cam != null) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            Log.e("FOO", size.width + "x" + size.height);

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private void initPreview(int w, int h) {
        Camera.Parameters params = cam.getParameters();

        pSize = params.getSupportedPreviewSizes().get(0);//getOptimalPreviewSize(params.getSupportedPreviewSizes(), w, h);
        Log.e("FOO", pSize.width + "x" + pSize.height);
        iSize = params.getSupportedPictureSizes().get(0);

        params.setPreviewSize(pSize.width, pSize.height);

        params.setPictureSize(iSize.width, iSize.height);
        params.setJpegQuality(100);

        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        cam.setDisplayOrientation((info.orientation + 360) % 360);
        params.setRotation(180);

        cam.setParameters(params);

        try {
            cam.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        initPreview(w, h);
        cam.startPreview();
    }
}
