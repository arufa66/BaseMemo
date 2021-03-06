package com.example.hitroki.basememo;

import android.provider.BaseColumns;

/**
 * Created by hitroki on 2015/02/12.
 */
public class MyContract {
    public static abstract class Memos implements BaseColumns {
        public static String TABLE_NAME ="memo";
        public static String COLUMN_ID ="_id";
        public static String COLUMN_TITLE ="title";
        public static String COLUMN_BODY ="_body";
        public static String COLUMN_CATEGORY ="category";
        public static String COLUMN_CREATED ="created";
        public static String COLUMN_UPDATED ="updated";

        public static String CREATE_TABLE =
                "create table " + TABLE_NAME +" (" +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_TITLE + " text, " +
                        COLUMN_BODY  + " text, " +
                        COLUMN_CREATED  + " datetime default current_timestamp, " +
                        COLUMN_UPDATED  + " datetime default current_timestamp, " +
                        COLUMN_CATEGORY + " text default 'default')" ;
        public static String INIT_TABLE =
                "insert into " + TABLE_NAME + " ("+ COLUMN_TITLE + " , " +COLUMN_BODY + " , " + COLUMN_CATEGORY + ") values " +
                        "('title1','body1','default'), ('title2','body2','default')";
        public static String DROP_TABLE =
                "drop table if exists " + TABLE_NAME;

    }
}
