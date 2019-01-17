package com.appjombang.myskripsi.Tabel;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appjombang.myskripsi.R;
import com.appjombang.myskripsi.sql.DataHelper;
import com.github.mikephil.charting.data.LineData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tabelmenit extends AppCompatActivity{

    protected Cursor cursor;
    DataHelper dbHelper;
    private RecyclerView mRecyclerView;
    private TampDataAdapter mTampDataAdapter;
    private ArrayList<TampData> mTampDataList;
    private RequestQueue mRequestQueue;
    private TextView timetabel;
    private int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tabelmnt);

        //Database
        dbHelper = new DataHelper(this);

        timetabel = (TextView) findViewById(R.id.timetabeldetik);

        ImageButton backTabelmnt = (ImageButton) findViewById(R.id.backTabelmnt);
        backTabelmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerviewmnt);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTampDataList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);

        time=0;
        parseJSON();
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                new Thread(){
                    @Override
                    public void run() {
                        while (true){
                            try {
                                Thread.sleep(1000);
                                time+=1;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            reftime(time);
                            if (time==30){
                                mTampDataList.clear();
                                parseJSON();
                                time=0;
                            }

                        }
                    }
                }.start();
                return null;
            }
        }.execute();

    }

    private void parseJSON(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tampdata ORDER BY waktu DESC ",null);
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);

            String field1 = cursor.getString(0).toString();
            String field2 = cursor.getString(1).toString();
            String createdAt = cursor.getString(2).toString();

            mTampDataList.add(new TampData(field1,field2,createdAt));
        }

        mTampDataAdapter = new TampDataAdapter(this, mTampDataList);
        ref();
    }

    public void ref(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mTampDataAdapter);
                Snackbar snackbar = Snackbar.make(mRecyclerView, "Tabel Berhasil Disegarkan.", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
    }

    public void reftime(final int ntime){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                timetabel.setText(String.valueOf(ntime));

            }
        });
    }
}
