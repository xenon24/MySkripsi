package com.appjombang.myskripsi.Alarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID1 = "channel1";
    public static final String CHANNEL_ID2 = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID1,
                    "Alarm running Background Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID2,
                    "Alarm running Background Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
