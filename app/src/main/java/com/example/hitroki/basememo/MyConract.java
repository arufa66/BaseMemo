package com.example.hitroki.basememo;

import android.provider.BaseColumns;

/**
 * Created by hitroki on 2015/02/12.
 */
public class MyConract {
    public static abstract class Memos implements BaseColumns {
        public static String TABLE_NAME ="memo";
        public static String COLUMN_ID ="_id";
        public static String COLUMN_TITLE ="title";
        public static String COLUMN_BODY ="_body";
        public static String COLUMN_CREATED ="created";
        public static String COLUMN_UPDATED ="updated";

        public static String CREATE_TABLE =
                "create table " + TABLE_NAME +" (" +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_TITLE + " text, " +
                        COLUMN_BODY  + " text, " +
                        COLUMN_CREATED  + " datetime default current_timestamp, " +
                        COLUMN_UPDATED  + " datetime default current_timestamp)";
        public static String INIT_TABLE =
                "insert into " + TABLE_NAME + " ("+ COLUMN_TITLE + " , " +COLUMN_BODY +") values " +
                        "('title1','body1'), ('title2','body2')";
        public static String DROP_TABLE =
                "drop table if exists " + TABLE_NAME;

    }
}
