package com.kpj.timelachs;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kpj.timelachs.camera.CameraHandler;
import com.kpj.timelachs.gui.TimePicker;
import com.kpj.timelachs.network.SocketHandler;
import com.kpj.timelachs.timelapse.TimelapseHandler;
import com.kpj.timelachs.utils.IntentHandler;


public class MainActivity extends FragmentActivity {
    CameraHandler cam;
    SocketHandler sock;
    TimelapseHandler tila;
    IntentHandler inta;

    // shitty hack for pointer arguments
    int[] startTime = {-1, -1};
    int[] stopTime = {-1, -1};

    BroadcastReceiver starter;
    PendingIntent starterPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cam = new CameraHandler(this);
        inta = new IntentHandler(this);

        initUI();
    }

    public void initUI() {
        ((Button) findViewById(R.id.start_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimelapse();
            }
        });

        ((Button) findViewById(R.id.stop_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimelapse();
            }
        });

        ((Button) findViewById(R.id.starttime_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment frag = new TimePicker(startTime, (Button) findViewById(R.id.starttime_butt));
                frag.show(getFragmentManager(), "starttime");
            }
        });

        ((Button) findViewById(R.id.stoptime_butt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment frag = new TimePicker(stopTime, (Button) findViewById(R.id.stoptime_butt));
                frag.show(getFragmentManager(), "stoptime");
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
                if (sock != null)
                    sock.stop();
            }
        });
    }

    private void startTimelapse() {
        if (sock == null) {
            Toast.makeText(getApplicationContext(), "Please connect socket first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startTime[0] != -1) {
            int deltaSeconds = TimePicker.getTimeDeltaInSeconds(startTime);

            Toast.makeText(getApplicationContext(), "Starting in " + deltaSeconds + "s", Toast.LENGTH_SHORT).show();

            inta.addStarter(new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent i) {
                    startTimelapse();
                }
            }, deltaSeconds);

            startTime[0] = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Starting timelapse", Toast.LENGTH_SHORT).show();

            tila = new TimelapseHandler(Integer.parseInt(((EditText) findViewById(R.id.interval)).getText().toString()), cam);
            tila.start();
        }

        if (stopTime[0] != -1) {
            int deltaSeconds = TimePicker.getTimeDeltaInSeconds(stopTime);

            Toast.makeText(getApplicationContext(), "Stopping in " + deltaSeconds + "s", Toast.LENGTH_SHORT).show();

            inta.addStopper(new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent i) {
                    stopTimelapse();
                }
            }, deltaSeconds);

            stopTime[0] = -1;
        }
    }

    private void stopTimelapse() {
        if (tila != null) {
            Toast.makeText(getApplicationContext(), "Stopping timelapse", Toast.LENGTH_SHORT).show();
            tila.stop();
        }
    }
}
