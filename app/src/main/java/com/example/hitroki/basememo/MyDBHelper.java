package com.example.hitroki.basememo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hitroki on 2015/02/12.
 */
public class MyDBHelper  extends SQLiteOpenHelper {

    private static final String DB_NAME = "mymemos3.db";
    private static final int version = 3;
    public MyDBHelper(Context context) {
        super(context,DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyContract.Memos.CREATE_TABLE);
        db.execSQL(MyContract.Memos.INIT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MyContract.Memos.DROP_TABLE);
        onCreate(db);
    }
}
