package com.example.hitroki.basememo;

import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Created by hitroki on 2015/02/20.
 */
public class Category {
    //
    public static ArrayAdapter<String> setCategoryAdapter(Context context){
        Cursor cursor;
        ArrayAdapter<String> adapter;
        cursor = categoryQuery(context);

        adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item
        );


        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        if (cursor.moveToFirst()) {
            String category;


            do {
                category = cursor.getString(cursor.getColumnIndex(MyContract.Memos.COLUMN_CATEGORY));
                // アダプターに追加
                adapter.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();


        return adapter;
    }

    //
    private static Cursor categoryQuery(Context context){


        MyContentProvider contentProvider;
        String[] projection={
                MyContract.Memos.COLUMN_CATEGORY
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
    public static void setSpinnerSelection(Spinner spinner, String item) {

        int index = getSpinnerPosition(spinner,item);
        spinner.setSelection(index);
    }
    //
    public static int getSpinnerPosition(Spinner spinner, String item){
        SpinnerAdapter adapter = spinner.getAdapter();
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(item)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
