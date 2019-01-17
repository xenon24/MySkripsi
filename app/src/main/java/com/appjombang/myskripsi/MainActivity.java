package com.appjombang.myskripsi;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appjombang.myskripsi.Alarm.ButtonAlarm;
import com.appjombang.myskripsi.Bantuan.ButtonBantuan;
import com.appjombang.myskripsi.DataTerbaru.ButtonDataTerbaru;
import com.appjombang.myskripsi.Grafik.ButtonGrafik;
import com.appjombang.myskripsi.Tabel.ButtonTabel;
import com.appjombang.myskripsi.Tabel.Tabelmenit;
import com.appjombang.myskripsi.Tabel.TampData;
import com.appjombang.myskripsi.Tabel.TampDataAdapter;
import com.appjombang.myskripsi.Tentang.ButtonInfo;
import com.appjombang.myskripsi.sql.DataHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected Cursor cursor;
    DataHelper dbHelper;
    private CardView Tabel, Grafik, Alarm, Info;
    private ArrayList<TampData> mTampDataList;
    private RequestQueue mRequestQueue;
    private TextView suhuMainActi,kelembabanMainActi;
    private Thread t;

    //  public SwipeRefreshLayout swipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        //Database
        dbHelper = new DataHelper(this);

        // tampil actionbar
        Toolbar toolbar = (Toolbar ) findViewById ( R.id.app_menubar );
        setSupportActionBar ( toolbar );

        // inisialisasi button
        Tabel = (CardView) findViewById ( R.id.btTabel );
        Grafik = (CardView) findViewById ( R.id.btGrafik );
        Alarm = (CardView) findViewById ( R.id.btAlarm );
        Info = (CardView) findViewById ( R.id.btInfo );
        Tabel.setOnClickListener ( this );
        Grafik.setOnClickListener ( this );
        Alarm.setOnClickListener ( this );
        Info.setOnClickListener ( this );

        suhuMainActi= (TextView) findViewById ( R.id.suhuMainActi );
        kelembabanMainActi= (TextView) findViewById ( R.id.kelembabanMainactiviti );

        mTampDataList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                new Thread(){
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(45000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mTampDataList.clear();
                            parseJSON();
                        }
                    }
                }.start();
                return null;
            }
        }.execute();

    }

    private void parseJSON(){
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

                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                cursor = db.rawQuery("SELECT * FROM tampdata WHERE waktu = '"+createdAt+"'",null);
                                if (!cursor.moveToFirst()){
                                    ContentValues val = new ContentValues();
                                    val.put("suhu",field1);
                                    val.put("kelembaban",field2);
                                    val.put("waktu",createdAt);
                                    db.insert("tampdata",null,val);
                                    //db.insertWithOnConflict("tampdata",null,val,SQLiteDatabase.CONFLICT_REPLACE);
                                    //Toast.makeText(MainActivity.this, "Insert Data Sukses", Toast.LENGTH_LONG).show();
                                }

                                mTampDataList.add(new TampData(field1,field2,createdAt));

                            }

                            //Toast.makeText(MainActivity.this, "Tampil Data Sukses", Toast.LENGTH_LONG).show();
                            ref(mTampDataList.get(0).getSuhu(), mTampDataList.get(0).getKelembaban());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getApplicationContext(), "Tidak ada internet, tidak bisa menampilkan data.", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(suhuMainActi, "Tidak ada internet, tidak bisa menampilkan data.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        mRequestQueue.add(request);

    }


    public void ref(final String suhu, final String kelembaban){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                suhuMainActi.setText(suhu);
                kelembabanMainActi.setText(kelembaban);
                // Toast.makeText(MainActivity.this, "Tampil Data Sukses", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(suhuMainActi, "Data Berhasil Disegarkan.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    //actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater ();
        menuInflater.inflate ( R.menu.menu_actionbar, menu );
        return true;
    }

    //menu selec actionbar and switch menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()){
            case R.id.lihat_data_terbaru:
                Intent menuDataTerbaru = new Intent ( this, ButtonDataTerbaru.class );
                startActivity ( menuDataTerbaru );
                break;
            case R.id.bantuan:
                Intent menuBatuan = new Intent ( this, ButtonBantuan.class );
                startActivity ( menuBatuan );
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected ( item );
    }

    // home buton
    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId ()){
            case R.id.btTabel : i = new Intent ( this, ButtonTabel.class ); startActivity ( i ); break;
            case R.id.btGrafik : i = new Intent ( this, ButtonGrafik.class ); startActivity ( i ); break;
            case R.id.btAlarm : i = new Intent ( this, ButtonAlarm.class ); startActivity ( i ); break;
            case R.id.btInfo : i = new Intent ( this, ButtonInfo.class ); startActivity ( i ); break;
            default:break;
        }
    }

    // home exit
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );

        builder.setTitle ( getString( R.string.keluar) );
        builder.setMessage ( getString( R.string.keluar_message) );

        builder.setPositiveButton ( "Ya", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finishActivity(0);
                finish();
            }
        } );

        builder.setNegativeButton ( "Batal", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        } );
        AlertDialog dialog = builder.show ();
    }
}
