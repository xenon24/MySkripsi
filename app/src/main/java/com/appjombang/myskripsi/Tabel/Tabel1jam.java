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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tabel1jam extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    private RecyclerView mRecyclerView;
    private TampDataAdapter mTampDataAdapter;
    private ArrayList<TampData> mTampDataList;
    private ArrayList<TampData> rTampDataList;
    private ArrayList<TampData> rNewTampDataList;
    private RequestQueue mRequestQueue;
    private TextView timetabel;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tabel1jam);

        //Database
        dbHelper = new DataHelper(this);

        timetabel = (TextView) findViewById(R.id.timetabeljam);

        ImageButton backTabel1jam = (ImageButton) findViewById(R.id.backTabel1jam);
        backTabel1jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview1jam);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mTampDataList = new ArrayList<>();
        rTampDataList = new ArrayList<>();
        rNewTampDataList = new ArrayList<>();

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
                            if (time==3600){
                                mTampDataList.clear();
                                rTampDataList.clear();
                                rNewTampDataList.clear();
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

        String hour1;
        String hour2;
        int nTot;
        double totSuhu;
        double totKelem;
        String totDate;
        String date1;
        String date2;

        hour1 = mTampDataList.get(mTampDataList.size()-1).getWaktu().substring(11,13);
        date1 = mTampDataList.get(mTampDataList.size()-1).getWaktu().substring(0,10);
        nTot=0;
        totSuhu=0;
        totKelem=0;

        for (int j = mTampDataList.size()-1; j >=0 ; j--) {
            hour2 = mTampDataList.get(j).getWaktu().substring(11,13);
            date2 = mTampDataList.get(j).getWaktu().substring(0,10);
            if (hour1.equals(hour2) && date1.equals(date2)) {
                nTot +=1;
                totSuhu+=Double.parseDouble(mTampDataList.get(j).getSuhu());
                totKelem+=Double.parseDouble(mTampDataList.get(j).getKelembaban());

                if (j==0){
                    totDate = mTampDataList.get(j+1).getWaktu().substring(0,10);
                    rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+String.valueOf(Integer.valueOf(hour1)+1)+":00:00"));
                }

            }else {

                totDate = mTampDataList.get(j+1).getWaktu().substring(0,10);
                rTampDataList.add(new TampData((String.format("%.2f",totSuhu/nTot)).replace(",","."),(String.format("%.2f",totKelem/nTot)).replace(",","."),totDate+" "+String.valueOf(Integer.valueOf(hour1)+1)+":00:00"));

                nTot=1;
                totSuhu= Double.valueOf(mTampDataList.get(j).getSuhu());
                totKelem= Double.valueOf(mTampDataList.get(j).getKelembaban());
            }

            hour1 = mTampDataList.get(j).getWaktu().substring(11,13);
            date1= mTampDataList.get(j).getWaktu().substring(0,10);


        }

        for (int i=rTampDataList.size()-1;i>=0;i--){
            rNewTampDataList.add(new TampData(rTampDataList.get(i).getSuhu(),rTampDataList.get(i).getKelembaban(),rTampDataList.get(i).getWaktu()));
        }

        mTampDataAdapter = new TampDataAdapter(Tabel1jam.this, rNewTampDataList);
        ref();

    }

    public void ref(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mTampDataAdapter);
                Snackbar snackbar = Snackbar.make(mRecyclerView, "Tabel Per Jam Berhasil Disegarkan.", Snackbar.LENGTH_LONG);
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
