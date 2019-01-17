package com.appjombang.myskripsi.Alarm;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appjombang.myskripsi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import static com.appjombang.myskripsi.Alarm.App.CHANNEL_ID1;
import static com.appjombang.myskripsi.Alarm.App.CHANNEL_ID2;
public class BackgroundService extends Service {
    private NotificationManagerCompat notificationManager;
    private ArrayList<TampData> mTampDataList;
    private ArrayList<TampData> rTampDataList;
    private ArrayList<TampData> rNewTampDataList;
    private RequestQueue mRequestQueue;
    private Thread t;
    private boolean te=true;
    boolean jns_alarm;
    String btssuhu,btsklmbbn;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = NotificationManagerCompat.from(this);
        mTampDataList = new ArrayList<>();
        rTampDataList = new ArrayList<>();
        rNewTampDataList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        t = new Thread(){
            @Override
            public void run(){
                while (!isInterrupted()&& te){
                    try {
                        Thread.sleep(3600000);

                        if (jns_alarm){
                            parseJSONTrue();

                        }else{
                            parseJSONFalse();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };

        t.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        jns_alarm=intent.getBooleanExtra("jns_alarm",true);
        btssuhu=intent.getStringExtra("btssuhu");
        btsklmbbn=intent.getStringExtra("btsklmbbn");

        Intent notificationIntent = new Intent(this, ButtonAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID1)
                .setContentTitle("Alarm Background Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        te=false;
        t.interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void warningAlert(String suhu, String kelembaban){

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID2)
                .setContentTitle("Peringatan!!!")
                .setContentText("Suhu: "+suhu+" dan Kelembaban: "+kelembaban)
                .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                .build();

        notificationManager.notify(2,notification);
    }

    private void parseJSONTrue(){

        String url = "https://api.thingspeak.com/channels/479855/feeds.json?api_key=8DVBNZAWZAPA4TS7&timezone=Asia/Jakarta";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("feeds");

                            for (int i = jsonArray.length () - 1; i >= 0; i--) {
                                JSONObject feed = jsonArray.getJSONObject ( i );

                                String field1 = feed.getString ( "field1" );
                                String field2 = feed.getString ( "field2" );
                                String createdAt = feed.getString ( "created_at" );

                                createdAt = createdAt.substring(0,10)+" "+createdAt.substring(11,19);

                                mTampDataList.add(new TampData(field1,field2,createdAt));

                            }

                            String hour1;
                            String hour2;
                            int nTot;
                            double totSuhu;
                            double totKelem;
                            String totDate;

                            hour1 = mTampDataList.get(mTampDataList.size()-1).getWaktu().substring(11,13);;
                            nTot=0;
                            totSuhu=0;
                            totKelem=0;

                            for (int j = mTampDataList.size()-1; j >=0 ; j--) {
                                hour2 = mTampDataList.get(j).getWaktu().substring(11,13);

                                if (hour1.equals(hour2)) {
                                    nTot +=1;
                                    totSuhu+=Double.parseDouble(mTampDataList.get(j).getSuhu());
                                    totKelem+=Double.parseDouble(mTampDataList.get(j).getKelembaban());

                                    if (j==0){
                                        totDate = mTampDataList.get(j).getWaktu().substring(0,10);
                                        rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+String.valueOf(Integer.valueOf(hour2)+1)+":00:00"));
                                    }

                                }else {
                                    totDate = mTampDataList.get(j).getWaktu().substring(0,10);
                                    rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+(hour2)+":00:00"));
                                    nTot=1;
                                    totSuhu= Double.valueOf(mTampDataList.get(j).getSuhu());
                                    totKelem= Double.valueOf(mTampDataList.get(j).getKelembaban());
                                }

                                hour1 = mTampDataList.get(j).getWaktu().substring(11,13);
                            }

                            for (int i=rTampDataList.size()-1;i>=0;i--){
                                rNewTampDataList.add(new TampData(rTampDataList.get(i).getSuhu(),rTampDataList.get(i).getKelembaban(),rTampDataList.get(i).getWaktu()));
                            }

                            if ((Double.parseDouble(rNewTampDataList.get(0).getSuhu())>45 | Double.parseDouble(rNewTampDataList.get(0).getKelembaban())>50) && te == true ){
                                String suhualert=rNewTampDataList.get(0).getSuhu();
                                String kelembabanalert=rNewTampDataList.get(0).getKelembaban();

                                warningAlert(suhualert,kelembabanalert);

                                Intent i = new Intent(BackgroundService.this,AlarmReceiver.class);
                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0,i,0);
                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pi);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Tidak ada internet, tidak bisa menampilkan data.", Toast.LENGTH_LONG).show();
                //error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }

    private void parseJSONFalse(){
        String url = "https://api.thingspeak.com/channels/479855/feeds.json?api_key=8DVBNZAWZAPA4TS7&timezone=Asia/Jakarta";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("feeds");
                            for (int i = jsonArray.length () - 1; i >= 0; i--) {
                                JSONObject feed = jsonArray.getJSONObject ( i );
                                String field1 = feed.getString ( "field1" );
                                String field2 = feed.getString ( "field2" );
                                String createdAt = feed.getString ( "created_at" );
                                createdAt = createdAt.substring(0,10)+" "+createdAt.substring(11,19);
                                mTampDataList.add(new TampData(field1,field2,createdAt));
                            }

                            String hour1;
                            String hour2;
                            int nTot;
                            double totSuhu;
                            double totKelem;
                            String totDate;

                            hour1 = mTampDataList.get(mTampDataList.size()-1).getWaktu().substring(11,13);;
                            nTot=0;
                            totSuhu=0;
                            totKelem=0;

                            for (int j = mTampDataList.size()-1; j >=0 ; j--) {
                                hour2 = mTampDataList.get(j).getWaktu().substring(11,13);
                                if (hour1.equals(hour2)) {
                                    nTot +=1;
                                    totSuhu+=Double.parseDouble(mTampDataList.get(j).getSuhu());
                                    totKelem+=Double.parseDouble(mTampDataList.get(j).getKelembaban());

                                    if (j==0){
                                        totDate = mTampDataList.get(j).getWaktu().substring(0,10);
                                        rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+String.valueOf(Integer.valueOf(hour2)+1)+":00:00"));
                                    }
                                }else {
                                    totDate = mTampDataList.get(j).getWaktu().substring(0,10);
                                    rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+(hour2)+":00:00"));
                                    nTot=1;
                                    totSuhu= Double.valueOf(mTampDataList.get(j).getSuhu());
                                    totKelem= Double.valueOf(mTampDataList.get(j).getKelembaban());
                                }
                                hour1 = mTampDataList.get(j).getWaktu().substring(11,13);
                            }

                            for (int i=rTampDataList.size()-1;i>=0;i--){
                                rNewTampDataList.add(new TampData(rTampDataList.get(i).getSuhu(),rTampDataList.get(i).getKelembaban(),rTampDataList.get(i).getWaktu()));
                            }

                            if (((Double.parseDouble(rNewTampDataList.get(0).getSuhu())>Double.parseDouble(btssuhu) | Double.parseDouble(rNewTampDataList.get(0).getKelembaban())>Double.parseDouble(btsklmbbn))) && te == true){
                                String suhualert=rNewTampDataList.get(0).getSuhu();
                                String kelembabanalert=rNewTampDataList.get(0).getKelembaban();

                                warningAlert(suhualert,kelembabanalert);

                                Intent i = new Intent(BackgroundService.this,AlarmReceiver.class);
                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0,i,0);
                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),pi);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Tidak ada internet, tidak bisa menampilkan data.", Toast.LENGTH_LONG).show();
                //error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
}
