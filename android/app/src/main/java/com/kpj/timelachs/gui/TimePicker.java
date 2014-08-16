package com.kpj.timelachs.gui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;

import java.util.Calendar;

public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    Button butt;
    int[] time;

    public TimePicker(int[] t, Button b) {
        butt = b;
        time = t;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // default to now()
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hour, int minute) {
        time[0] = hour;
        time[1] = minute;

        butt.setText(hour + ":" + minute);
    }

    public static int getTimeDeltaInSeconds(int[] time) {
        final Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);

        return ((time[1] - m) + (time[0] - h) * 60) * 60;
    }
}
