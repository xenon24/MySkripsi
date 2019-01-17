package com.appjombang.myskripsi.DataTerbaru;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.appjombang.myskripsi.MainActivity;
import com.appjombang.myskripsi.R;
import com.appjombang.myskripsi.Tabel.TampData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ButtonDataTerbaru extends AppCompatActivity  {

    private ArrayList<TampData> mTampDataList;
    private ArrayList<TampData> rTampDataList;
    private ArrayList<TampData> rNewTampDataList;
    private RequestQueue mRequestQueue;
    private TextView suhuMainActimnt,kelembabanMainActimnt,suhuMainActijam,kelembabanMainActijam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_data_terbaru );

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backLihatDataTerbaru);
        backBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        suhuMainActimnt= (TextView) findViewById ( R.id.Suhuterbarumnt );
        kelembabanMainActimnt= (TextView) findViewById ( R.id.Kelembabanterbarumnt );
        suhuMainActijam= (TextView) findViewById ( R.id.Suhuterbaru1jam );
        kelembabanMainActijam= (TextView) findViewById ( R.id.Kelembabanterbaru1jam );

        /*
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayoutTerbaru);
        swipeRefreshLayout.setOnRefreshListener(this);
        */

        mTampDataList = new ArrayList<>();
        rTampDataList = new ArrayList<>();
        rNewTampDataList = new ArrayList<>();
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
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mTampDataList.clear();
                            rTampDataList.clear();
                            rNewTampDataList.clear();
                            parseJSON();
                        }
                    }
                }.start();
                return null;
            }
        }.execute();


    }

    private void parseJSON(){
        /*
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Silakan Tunggu...");
        progressDialog.show();
        swipeRefreshLayout.setRefreshing(true);
        */

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

                            /*
                            suhuMainActimnt.setText(mTampDataList.get(0).getSuhu());
                            kelembabanMainActimnt.setText(mTampDataList.get(0).getKelembaban());

                            suhuMainActijam.setText(rNewTampDataList.get(0).getSuhu());
                            kelembabanMainActijam.setText(rNewTampDataList.get(0).getKelembaban());

                            progressDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            */

                            ref(mTampDataList.get(0).getSuhu(), mTampDataList.get(0).getKelembaban());
                            refjam(rNewTampDataList.get(0).getSuhu(), rNewTampDataList.get(0).getKelembaban());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                 */
               // Toast.makeText(getApplicationContext(), "Tidak ada internet, tidak bisa menampilkan data.", Toast.LENGTH_LONG).show();
                //error.printStackTrace();

                Snackbar snackbar = Snackbar.make(suhuMainActijam, "Tidak ada internet, tidak bisa menampilkan data.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        mRequestQueue.add(request);
    }


    public void ref(final String suhu, final String kelembaban){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                suhuMainActimnt.setText(suhu);
                kelembabanMainActimnt.setText(kelembaban);
            }
        });
    }

    public void refjam(final String suhu, final String kelembaban){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                suhuMainActijam.setText(suhu);
                kelembabanMainActijam.setText(kelembaban);
                //Toast.makeText(ButtonDataTerbaru.this, "Tampil Data Sukses", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(suhuMainActijam, "Data Terbaru Berhasil Disegarkan.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }
}
