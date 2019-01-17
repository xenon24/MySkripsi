package com.appjombang.myskripsi.Alarm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Alarm Bunyi",Toast.LENGTH_LONG).show();

        Intent serviceIntent = new Intent(context,RingtonePlayingService.class);
        context.startService(serviceIntent);

        //Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        //v.vibrate(10000);
    }
}
