package com.appjombang.myskripsi.Grafik;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GrafikKel1Jam extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    protected Cursor cursor;
    DataHelper dbHelper;


    private static final String TAG = "GrafikKel1Jam";
    private LineChart mChart;
    private ArrayList<TampData> mTampDataList;
    private ArrayList<TampData> rTampDataList;
    private ArrayList<TampData> rNewTampDataList;
    private RequestQueue mRequestQueue;
    private TextView timegraph;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grafikkel1jam);

        dbHelper = new DataHelper(this);

        timegraph = (TextView) findViewById(R.id.timegrap1jamkelembaban);

        ImageButton Grafikkel1jam = (ImageButton) findViewById(R.id.backGrafikkel1jam);
        Grafikkel1jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        LinearLayout intentsuhuback1jam = (LinearLayout) findViewById(R.id.gsuhupindah1jam);
        intentsuhuback1jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        mChart = (LineChart) findViewById(R.id.lineChartkel1jam);

        mChart.setOnChartGestureListener(GrafikKel1Jam.this);
        //mChart.setOnChartValueSelectedListener(MainActivity.this);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        LimitLine upper_limit = new LimitLine(81,"Danger");
        upper_limit.setLineWidth(1f);
        upper_limit.enableDashedLine(10f,10f,0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);
        upper_limit.setTextColor(Color.RED);

        LimitLine lower_limit = new LimitLine(60,"too Low");
        lower_limit.setLineWidth(1f);
        lower_limit.enableDashedLine(10f,10f,0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);
        lower_limit.setTextColor(Color.RED);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f,10f,0);
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();

        int j=0;
        for (int i = rNewTampDataList.size()-1; i >0 ; i--) {
            yValues.add(new Entry(j,Float.parseFloat(rNewTampDataList.get(i).getKelembaban())));
            j+=1;
        }


        String[] values = new String[rNewTampDataList.size()];

        int k=0;
        for (int i = rNewTampDataList.size()-1; i > 0; i--) {
            values[k]=rNewTampDataList.get(i).getWaktu();
            k+=1;
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new GrafikKel1Jam.MyXAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(90);

        LineDataSet set1 = new LineDataSet(yValues,"Kelembaban");

        set1.setDrawIcons(false);

        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f);

        // black lines and points
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);

        // line thickness and point size
        set1.setLineWidth(1f);
        set1.setCircleRadius(1.4f);

        // draw points as solid circles
        set1.setDrawCircleHole(false);

        // customize legend entry
        set1.setFormLineWidth(1f);
        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set1.setFormSize(15.f);

        // text size of values
        set1.setValueTextSize(9f);

        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        set1.setDrawFilled(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mChart.getAxisLeft().getAxisMinimum();
            }
        });

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);
        ref(data);

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i(TAG,"onChartGestureStart: X: "+me.getX()+"Y: "+me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i(TAG,"onChartGestureEnd: "+lastPerformedGesture);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i(TAG,"onChartLongPressed: ");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i(TAG,"onChartDoubleTapped: ");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i(TAG,"onChartSingleTapped: ");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i(TAG,"onChartFling: veloX: "+velocityX+"veloY: "+velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i(TAG,"onChartScale: scaleX: "+scaleX+"scaleY: "+scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i(TAG,"onChartTranslate: dX: "+dX+"dY: "+dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        /*Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());*/
        String msg = "Suhu: "+e.getX()+" Waktu: "+e.getY();
        Toast.makeText(GrafikKel1Jam.this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected() {
        Log.i(TAG, "onNothingSelected: ");
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mvalues;

        public MyXAxisValueFormatter(String[] values){
            this.mvalues=values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mvalues[(int)value];
        }
    }

    public void ref(final LineData data){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.setData(data);
                mChart.animateX(4000);
                Snackbar snackbar = Snackbar.make(mChart, "Grafik Perjam Kelembaban Berhasil Disegarkan.", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
    }

    public void reftime(final int ntime){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timegraph.setText(String.valueOf(ntime));

            }
        });
    }
}
