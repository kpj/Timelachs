package com.kpj.timelachs;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kpj.timelachs.timelapse.TimelapseHandler;
import com.kpj.timelachs.camera.CameraHandler;
import com.kpj.timelachs.network.SocketHandler;

import static android.view.View.resolveSize;


public class MainActivity extends Activity {
    CameraHandler cam;
    SocketHandler sock;
    TimelapseHandler tila;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cam = new CameraHandler(this);

        initUI();
    }

    public void initUI() {
        ((Button) findViewById(R.id.start_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sock == null) {
                    Toast.makeText(getApplicationContext(), "Please connect socket first", Toast.LENGTH_SHORT).show();
                    return;
                }
                tila = new TimelapseHandler(Integer.parseInt(((EditText) findViewById(R.id.interval)).getText().toString()), cam);

                Toast.makeText(getApplicationContext(), "Starting timelapse", Toast.LENGTH_SHORT).show();

                tila.start();
            }
        });

        ((Button) findViewById(R.id.stop_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tila != null) {
                    Toast.makeText(getApplicationContext(), "Stopping timelapse", Toast.LENGTH_SHORT).show();
                    tila.stop();
                }
            }
        });

        ((Button) findViewById(R.id.connect_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ((EditText) findViewById(R.id.ip)).getText().toString();
                int port = Integer.parseInt(((EditText) findViewById(R.id.port)).getText().toString());

                sock = new SocketHandler(getApplicationContext(), ip, port);
                cam.setSocket(sock);
            }
        });

        ((Button) findViewById(R.id.disconnect_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sock != null)
                    sock.stop();
            }
        });
    }
}
