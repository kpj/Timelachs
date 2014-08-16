package com.kpj.timelachs.timelapse;

import android.os.Handler;

import com.kpj.timelachs.camera.CameraHandler;

public class TimelapseHandler {
    int interval;
    CameraHandler cam;

    Handler handler;
    LapseThread thread;

    public TimelapseHandler(int iv, CameraHandler c) {
        interval = iv;
        cam = c;

        handler = new Handler();
    }

    public void start() {
        stop();

        thread = new LapseThread();
        handler.postAtFrontOfQueue(thread);
    }

    public void stop() {
        if(thread != null)
            handler.removeCallbacks(thread);
    }

    class LapseThread implements Runnable {
        @Override
        public void run() {
            cam.shoot();
            handler.postDelayed(this, interval * 1000);
        }
    }
}
