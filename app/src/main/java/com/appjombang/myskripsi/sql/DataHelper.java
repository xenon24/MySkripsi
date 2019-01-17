package com.appjombang.myskripsi.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.appjombang.myskripsi.model.TampData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by delaroy on 5/10/17.
 */
public class DataHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "TampdataManager.db";

    public DataHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String sql = "create table tampdata ( suhu TEXT NULL, kelembaban TEXT NULL, waktu TEXT NULL);";
        Log.d("Data","onCreate: "+sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db1, int oldVersion, int newVersion) {

    }


}
