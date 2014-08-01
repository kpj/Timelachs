package com.kpj.timelachs.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketHandler {
    Context ctx;

    Socket sock;
    OutputStream out;

    String ip;
    int port;

    public SocketHandler(Context c, String i, int p) {
        ctx = c;
        ip = i;
        port = p;

        new Thread(new ClientThread()).start();
    }

    public void stop() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sock = null;
    }

    public void sendBytes(byte[] arr) {
        if(sock != null) {
            try {
                // send size of following data in 4 bytes
                byte[] bytes = ByteBuffer.allocate(4).putInt(arr.length).array();
                out.write(bytes);

                out.write(arr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ctx, "Network broken", Toast.LENGTH_SHORT).show();
            Log.e("FOO", "No socket to send bytes to available");
        }
    }

    private class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                sock = new Socket(ip, port);

                out = sock.getOutputStream();
                Log.e("FOO", "Socket connected");
                //Toast.makeText(ctx, "Socket connection succeeded", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("FOO", "Socket connection failed");
                //Toast.makeText(ctx, "Socket connection failed", Toast.LENGTH_SHORT).show();
                sock = null;
                e.printStackTrace();
            }
        }
    }
}
