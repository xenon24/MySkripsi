package com.appjombang.myskripsi.Tentang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.appjombang.myskripsi.R;

public class ButtonInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.layout_buttoninfo );

        ImageButton backBantuan = (ImageButton) findViewById(R.id.backTentang);
        backBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
