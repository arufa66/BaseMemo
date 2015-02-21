package com.example.hitroki.basememo;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks,
        AdapterView.OnItemSelectedListener {
  private   SimpleCursorAdapter adapter;
    private Spinner myCategorySpinner;
    private final String SORT =  "updated desc";

   private ArrayAdapter<String> categoryAdapter;
    private final String[] POJECTION = {
            MyConract.Memos.COLUMN_ID,
            MyConract.Memos.COLUMN_TITLE,
            MyConract.Memos.COLUMN_UPDATED,
            MyConract.Memos.COLUMN_CATEGORY
};


    private long memoId;
    public final static String EXTRA_MYID ="com.dotinstall.taguchi.mymemoapp.MYID";
    private final static String ALL = "すべて";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCategorySpinner = (Spinner)findViewById(R.id.category);


        categoryAdapter = Category.setCategoryAdapter(this);
        categoryAdapter.add(ALL);
        myCategorySpinner.setAdapter(categoryAdapter);
        myCategorySpinner.setOnItemSelectedListener(this);

        String[] from={
                MyConract.Memos.COLUMN_TITLE,
                MyConract.Memos.COLUMN_CATEGORY
        };
        int[] to = {R.id.text1,
                R.id.text2};


        //メモのリストのアダプター
        adapter = new SimpleCursorAdapter(
                this,
               R.layout.row,
                null,
                from,
                to,
                0
        );


        //メモ一覧のリストビュー
        ListView myListView = (ListView)findViewById(R.id.myListView);

        SwipeDismissAdapter swipeDismissAdapter = new SwipeDismissAdapter(adapter,
                new OnDismissCallback() {

                    @Override
                    public void onDismiss(@NonNull ViewGroup listView, @NonNull final int[] reverseSortedPositions) {

                                for(int position : reverseSortedPositions) {
                                    memoId = adapter.getItemId(position);
                                    Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
                                    String selection = MyConract.Memos.COLUMN_ID + " = ?";
                                    String selectionArgs[] = new String[]{Long.toString(memoId)};
                                    getContentResolver().delete(
                                            uri,
                                            selection,
                                            selectionArgs
                                    );
                                }
                    }
                });



        AnimationAdapter animationAdapter = new AlphaInAnimationAdapter(swipeDismissAdapter);
        animationAdapter.setAbsListView(myListView);
        myListView.setAdapter(animationAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view,int i,long l){

                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra(EXTRA_MYID,l);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(0,null,this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //メニューをクリックしたときの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //Add　Newをクリックした場合の処理
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return allView(POJECTION);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        adapter.swapCursor((android.database.Cursor) data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }

    @Override
    //TODO:スピナーがクリックした場合の処理を書く。
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor;
       if (position  == Category.getSpinnerPosition(myCategorySpinner, ALL)){


           getLoaderManager().restartLoader(0,null,this);

       }else{
           String selection = MyConract.Memos.COLUMN_CATEGORY + " = ?";
           String[] selectionArgs = new String[]{(String)myCategorySpinner.getSelectedItem()};

           cursor = getContentResolver().query(
                   MyContentProvider.CONTENT_URI,
                   POJECTION,
                   selection,
                   selectionArgs,
                   SORT
           );
           adapter.swapCursor(cursor);

       }







    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private Loader allView(String[] projection){

        return new CursorLoader(
                this,
                MyContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                SORT
        );
    }
}
