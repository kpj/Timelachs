package com.kpj.timelachs.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import com.kpj.timelachs.R;
import com.kpj.timelachs.network.SocketHandler;

public class CameraHandler {
    Context ctx;

    Camera mCamera;
    Preview mPreview;

    SocketHandler sock;

    public CameraHandler(Context c) {
        ctx = c;

        mPreview = new Preview(c);
        sock = null;

        if(safeCameraOpen(0)) {
            mPreview.setCamera(mCamera);
        }
    }

    public void shoot() {
        mCamera.takePicture(null, null, null, new PicCallback());
    }

    public void setSocket(SocketHandler s) {
        sock = s;
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e(String.valueOf(R.string.app_name), "Failed to open camera");
            e.printStackTrace();
        }

        return qOpened;
    }

    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private class PicCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            if(bytes == null) {
                Log.e("FOO", "Something went wrong");
            } else {
                Log.e("FOO", "Took image: " + bytes.length);

                if(sock != null) {
                    sock.sendBytes(bytes);
                } else {
                    Toast.makeText(ctx, "Please connect socket first", Toast.LENGTH_SHORT).show();
                }
            }

            mCamera.startPreview();
        }
    }
}
