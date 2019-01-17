package com.appjombang.myskripsi.Bantuan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import com.appjombang.myskripsi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ButtonBantuan extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataParent;
    private HashMap<String, List<String>> listHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_bantuan );

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backBantuan);
        backBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView = (ExpandableListView) findViewById(R.id.expand);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataParent, listHash);
        listView.setAdapter(listAdapter);
    }

    private void initData() {

        listDataParent = new ArrayList<>();
        listHash = new HashMap<>();
        listDataParent.add("Lihat Data Terbaru");
        listDataParent.add("Tabel");
        listDataParent.add("Grafik");
        listDataParent.add("Alarm");
        listDataParent.add("Kontak Bantuan");


        List<String> lDataNew = new ArrayList<>();
        lDataNew.add("Lihat data terbaru adalah sebuah halaman yang berisi data terbaru yang akan menampilkan data setiap 30 detik dan data per 1 jam dari data suhu dan kelembaban ruangan greenhouse.");

        List<String> tabel = new ArrayList<>();
        tabel.add("Tabel adalah sebuah halaman yang berisi data  yang akan menampilkan data suhu dan kelembaban setiap 30 detik dan data per 1 jam dalam bentuk tabel RecyclerView.");

        List<String> grafik = new ArrayList<>();
        grafik.add("Grafik adalah sebuah halaman yang berisi data  yang akan menampilkan data suhu dan kelembaban setiap 30 detik dan data per 1 jam dalam bentuk grafik LineChart.");

        List<String> alarm = new ArrayList<>();
        alarm.add("Alarm adalah sebuah halaman yang didalamnya kita dapat mengatur alarm suhu dan kelembaban maximum ruangan greenhouse baik secara default(untuk buah melon) maupun manual.");

        List<String> konBan = new ArrayList<>();
        konBan.add("Apabila anda masih merasa kesulitan dalam menggunakan aplikasi ini, anda bisa menghubungi kontak dibawah ini :" +"\n" +"\n"+
                "No.Tlp/WA : 085746442170" +"\n"+
                "Email : 04rizal@gmail.com");

        listHash.put(listDataParent.get(0), lDataNew);
        listHash.put(listDataParent.get(1), tabel);
        listHash.put(listDataParent.get(2), grafik);
        listHash.put(listDataParent.get(3), alarm);
        listHash.put(listDataParent.get(4), konBan);

    }
}

