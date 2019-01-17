package com.appjombang.myskripsi.Tabel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;

import com.appjombang.myskripsi.R;

public class ButtonTabel extends AppCompatActivity implements View.OnClickListener {

    private CardView Tabel1jam, Tabelmnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_buttontable );

        Tabel1jam = (CardView) findViewById(R.id.btTabel1jam);
        Tabelmnt = (CardView) findViewById(R.id.btTabel2mnt);
        Tabelmnt.setOnClickListener(this);
        Tabel1jam.setOnClickListener(this);

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backTabel);
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
            case R.id.btTabel2mnt : i = new Intent ( this, Tabelmenit.class ); startActivity ( i ); break;
            case R.id.btTabel1jam : i = new Intent ( this, Tabel1jam.class ); startActivity ( i ); break;
            default:break;
        }

    }
}
