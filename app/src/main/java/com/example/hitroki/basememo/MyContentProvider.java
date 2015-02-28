package com.example.hitroki.basememo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by hitroki on 2015/02/12.
 */
public class MyContentProvider extends ContentProvider {
    private static String AUTHORITY ="com.example.hitroki.basememo.mycontentprovider";
    private MyDBHelper myDBHelper;
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + MyContract.Memos.TABLE_NAME);

    private static final int MEMOS = 1;
    private static  final  int MEMO_ITEM = 2;

    private  static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MyContract.Memos.TABLE_NAME,MEMOS);
        uriMatcher.addURI(AUTHORITY, MyContract.Memos.TABLE_NAME + "/#",MEMO_ITEM);
    }
    @Override
    public boolean onCreate() {
        myDBHelper = new MyDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        if(uriMatcher.match(uri) != MEMOS){
//            throw new IllegalArgumentException("Unknown URI:" + uri);
//        }
        switch (uriMatcher.match(uri)){
            case MEMOS:
            case MEMO_ITEM:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI:" + uri);

        }
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor cursor = db.query(
                true,
                MyContract.Memos.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder,
                null
        );
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(uriMatcher.match(uri) != MEMOS){
            throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        SQLiteDatabase db = myDBHelper.getWritableDatabase();

        long newId = db.insert(
                MyContract.Memos.TABLE_NAME,
                null,
                values
        );
        Uri newUri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, newId);
        getContext().getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(uriMatcher.match(uri) != MEMO_ITEM){
            throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        int count = db.delete(MyContract.Memos.TABLE_NAME,
                selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(uriMatcher.match(uri) != MEMO_ITEM){
            throw new IllegalArgumentException("Unknown URI:" + uri);
        }
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        int count = db.update(MyContract.Memos.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
