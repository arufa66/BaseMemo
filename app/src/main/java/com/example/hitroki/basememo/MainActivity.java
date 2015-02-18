package com.example.hitroki.basememo;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
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

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks,
        AdapterView.OnItemSelectedListener {
  private   SimpleCursorAdapter adapter;
    private Spinner categorySpinner;
    private ArrayList<String> arrayList;
   private ArrayAdapter<String> categoryAdapter;

    private long memoId;
    public final static String EXTRA_MYID ="com.dotinstall.taguchi.mymemoapp.MYID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categorySpinner = (Spinner)findViewById(R.id.category);
        arrayList = new ArrayList<String>();

         categoryAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                arrayList);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        String[] from={
                MyConract.Memos.COLUMN_TITLE,
                MyConract.Memos.COLUMN_UPDATED
        };
        int[] to = {android.R.id.text1,
                android.R.id.text2};


        //メモのリストのアダプター
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
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


                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Delete Memo");
                        alertDialog.setMessage("Are you sure to delete this memo?");
                        alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override

                            //TODO: スワイプしたアダプターのidをとってくる。
                            public void onClick(DialogInterface dialog, int which) {
                                for(int position : reverseSortedPositions) {
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
                        alertDialog.show();
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
        String[] projection = {
                MyConract.Memos.COLUMN_ID,
                MyConract.Memos.COLUMN_TITLE,
                MyConract.Memos.COLUMN_UPDATED
        };
        return new CursorLoader(
                this,
                MyContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                "updated desc"
        );
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
