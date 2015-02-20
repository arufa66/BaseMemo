package com.example.hitroki.basememo;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by hitroki on 2015/02/20.
 */
public class DBCommand {
    public static Cursor categoryQuery(Context context){


        MyContentProvider contentProvider;
        String[] projection={
                MyConract.Memos.COLUMN_CATEGORY
        };

       Cursor cursor =context.getContentResolver().query(

               MyContentProvider.CONTENT_URI,
               projection,
               null,
               null,
               null
       );


        return cursor;
    }
}
