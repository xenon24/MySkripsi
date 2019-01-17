package com.appjombang.myskripsi.Grafik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;

import com.appjombang.myskripsi.R;

public class ButtonGrafik extends AppCompatActivity implements View.OnClickListener {

    private CardView Grafikmenit, Grafik1jam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_buttongrafik );

        Grafikmenit = (CardView) findViewById(R.id.btGrafik2mnt);
        Grafik1jam = (CardView) findViewById(R.id.btGrafik1jam);
        Grafik1jam.setOnClickListener(this);
        Grafikmenit.setOnClickListener(this);

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backGrafik);
        backBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.btGrafik2mnt : i = new Intent ( this, Grafikmenit.class ); startActivity ( i ); break;
            case R.id.btGrafik1jam : i = new Intent ( this, Grafik1jam.class ); startActivity ( i ); break;
            default:break;
        }
    }
}
