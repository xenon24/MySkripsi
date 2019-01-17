package com.appjombang.myskripsi.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appjombang.myskripsi.MainActivity;
import com.appjombang.myskripsi.R;
import com.appjombang.myskripsi.Tabel.TampData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ButtonAlarm extends AppCompatActivity {

    private ArrayList<TampData> mTampDataList;
    private ArrayList<TampData> rTampDataList;
    private RequestQueue mRequestQueue;
    private CardView btnHidup, btnMati;

    private TextView jenis_alarm;
    private TextView status_alarm;
    private String jn_alarm;
    private EditText bts_Suhu;
    private EditText bts_klmbaban;
    String filename="file.txt";
    String fileSuhu="suhu.txt";

    //Intent i;
    //AlarmManager am;

    LinearLayout tampil;
    String text,btsSuhu,btsKlmbbn;
    boolean isOn=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_buttonalarm);

        jenis_alarm = findViewById(R.id.jenis_alarm);
        status_alarm = findViewById(R.id.status_alarm);
        bts_Suhu=findViewById(R.id.edit_suhu);
        bts_klmbaban=findViewById(R.id.edit_kelembaban);
        btnHidup = findViewById(R.id.btnHidup);
        btnMati = findViewById(R.id.btnMati);
        btnHidup.setClickable(false);
        btnMati.setClickable(false);

        String suhu = readfile(fileSuhu);
        if (suhu.length() > 0){
            String[] sk = suhu.split("\\s+");
            Log.i("suhu", suhu);
            btsSuhu = sk[0];
            btsKlmbbn = sk[1];
            bts_Suhu.setText(btsSuhu);
            bts_klmbaban.setText(btsKlmbbn);
            btnHidup.setClickable(true);
            btnMati.setClickable(true);
        }

        mTampDataList = new ArrayList<>();
        rTampDataList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backAlarm);
        backBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Switch melon = (Switch) findViewById(R.id.switchMelon);
        tampil = (LinearLayout) findViewById(R.id.layoutAlarm);

        melon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    Toast.makeText(getApplicationContext(), "Alarm buah melon ditambahkan",Toast.LENGTH_LONG).show();
                    tampil.setVisibility(View.INVISIBLE);
                    isOn=true;
                    jn_alarm="Alarm Buah Melon";
                    jenis_alarm.setText(jn_alarm);
                    btnHidup.setClickable(true);
                    btnMati.setClickable(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Alarm buah melon dikosongkan",Toast.LENGTH_LONG).show();
                    tampil.setVisibility(View.VISIBLE);
                    isOn=false;
                    jn_alarm="Alarm Kosong";
                    jenis_alarm.setText(jn_alarm);
                    btnHidup.setClickable(false);
                    btnMati.setClickable(false);

                    serviceIntent = new Intent(ButtonAlarm.this, BackgroundService.class);
                    serviceIntent.putExtra("inputExtra", jn_alarm);
                    serviceIntent.putExtra("jns_alarm",isOn);
                    serviceIntent.putExtra("btssuhu",btsSuhu);
                    serviceIntent.putExtra("btsklmbbn",btsKlmbbn);
                    if (serviceIntent != null){
                        stopService(serviceIntent);
                    }
                    status_alarm.setText("Alarm Mati");

                    String text2="m";
                    saveFile(filename,text2);
                }
            }
        });

        String textf=readfile(filename);

        if (textf.equals("me")){
            melon.setChecked(true);
            jn_alarm="Alarm Buah Melon";
            jenis_alarm.setText(jn_alarm);
            status_alarm.setText("Alarm Hidup");
        }else if (textf.equals("ma")){
            jn_alarm="Alarm Manual";
            jenis_alarm.setText(jn_alarm);
            status_alarm.setText("Alarm Hidup");

        }



    }

    public void tambahAlarmSekarang(View v){
        if (!(bts_Suhu.getText().toString().length()==0) && !(bts_klmbaban.getText().toString().length()==0)){
            jn_alarm="Alarm Manual";
            jenis_alarm.setText(jn_alarm);
            btnHidup.setClickable(true);
            btnMati.setClickable(true);
            btsSuhu=bts_Suhu.getText().toString();
            btsKlmbbn=bts_klmbaban.getText().toString();
            saveFile(fileSuhu, btsSuhu+" "+btsKlmbbn);
        }

    }

    public void hapusAlarmSekarang(View v){
        jn_alarm="Alarm Kosong";
        jenis_alarm.setText(jn_alarm);

        serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("inputExtra", jn_alarm);
        serviceIntent.putExtra("jns_alarm",isOn);
        serviceIntent.putExtra("btssuhu",btsSuhu);
        serviceIntent.putExtra("btsklmbbn",btsKlmbbn);
        if (serviceIntent != null){
            stopService(serviceIntent);
        }
        status_alarm.setText("Alarm Mati");
        btnHidup.setClickable(false);
        btnMati.setClickable(false);
        String text2="m";
        saveFile(filename,text2);
    }

    Intent serviceIntent;
    public void startService(View v){
            serviceIntent = new Intent(this, BackgroundService.class);
            serviceIntent.putExtra("inputExtra", jn_alarm);
            serviceIntent.putExtra("jns_alarm",isOn);
            serviceIntent.putExtra("btssuhu",btsSuhu);
            serviceIntent.putExtra("btsklmbbn",btsKlmbbn);
            ContextCompat.startForegroundService(this, serviceIntent);
            status_alarm.setText("Alarm Hidup");

            String text1="";
            if(isOn){
                text1="me";
            }else{
                text1="ma";
            }

            saveFile(filename,text1);

    }

    public void stopService(View v){
        //Intent serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("inputExtra", jn_alarm);
        serviceIntent.putExtra("jns_alarm",isOn);
        serviceIntent.putExtra("btssuhu",btsSuhu);
        serviceIntent.putExtra("btsklmbbn",btsKlmbbn);

        if (serviceIntent != null){
            stopService(serviceIntent);
            status_alarm.setText("Alarm Mati");

            String text2="m";
            saveFile(filename,text2);
        }

    }

    public void saveFile(String file,String text){
        try {
            FileOutputStream fos = openFileOutput(file,Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readfile(String file){
        String text="";
        try{
            FileInputStream is = openFileInput(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return text;
    }



}
