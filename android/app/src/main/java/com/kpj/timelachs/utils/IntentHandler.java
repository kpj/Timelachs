package com.kpj.timelachs.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

public class IntentHandler {
    Context ctx;

    AlarmManager alarmManager;

    Holder starter;
    Holder stopper;

    public IntentHandler(Context c) {
        ctx = c;
        alarmManager = (AlarmManager) (ctx.getSystemService(Context.ALARM_SERVICE));

        starter = null;
        stopper = null;
    }

    public void addStarter(BroadcastReceiver br, int sd) {
        if(starter != null)
            starter.cancel();
        starter = new Holder(br, sd, "starter");
    }
    public void addStopper(BroadcastReceiver br, int sd) {
        if(stopper != null)
            stopper.cancel();
        stopper = new Holder(br, sd, "stopper");
    }

    private class Holder {
        private BroadcastReceiver brecv;
        private PendingIntent peint;
        private Intent intent;

        public Holder(BroadcastReceiver br, int secDelay, String suffix) {
            brecv = br;

            ctx.registerReceiver(brecv, new IntentFilter("com.kpj.timelachs." +  suffix));
            intent = new Intent("com.kpj.timelachs." + suffix);

            peint = PendingIntent.getBroadcast(ctx, 0, intent, 0);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + secDelay * 1000, peint);
        }

        public void cancel() {
            PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
        }
    }
}
