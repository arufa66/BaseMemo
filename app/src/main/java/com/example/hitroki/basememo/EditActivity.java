package com.example.hitroki.basememo;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class EditActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private boolean isNewMemo = true;
    private long memoId;
    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private EditText myMemoTitle;
    private EditText myMemoBody;
    private TextView myMemoUpdated;
    private String title = "";
    private String body = "";
    private String updated = "";
     private String category = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        myMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        myMemoBody = (EditText) findViewById(R.id.myMemoBody);
        myMemoUpdated = (TextView) findViewById(R.id.Updated);
        categorySpinner = (Spinner)findViewById(R.id.category);

        Intent intent = getIntent();
        memoId = intent.getLongExtra(MainActivity.EXTRA_MYID, 0L);
        isNewMemo = memoId == 0L ? true : false;


            categoryAdapter = CategorySetting.setCategoryAdapter(this);
            categoryAdapter.add("新しいカテゴリを作成");
            categorySpinner.setAdapter(categoryAdapter);
            categorySpinner.setOnItemSelectedListener(this);


        if (isNewMemo) {
            //new memo
            setTitle("新しいメモ");
        } else {
            // edit memo
            setTitle("メモの編集");
            Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
            String[] projection = new String[]{
                    MyConract.Memos.COLUMN_TITLE,
                    MyConract.Memos.COLUMN_BODY,
                    MyConract.Memos.COLUMN_UPDATED,
                    MyConract.Memos.COLUMN_CATEGORY

            };
            String selection = MyConract.Memos.COLUMN_ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(memoId)};
            Cursor cursor = getContentResolver().query(uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndex(MyConract.Memos.COLUMN_TITLE));
                body = cursor.getString(cursor.getColumnIndex(MyConract.Memos.COLUMN_BODY));
                updated = "Updated: " + cursor.getString(cursor.getColumnIndex(MyConract.Memos.COLUMN_UPDATED));
            }
            cursor.close();
            myMemoTitle.setText(title);
            myMemoBody.setText(body);
            myMemoUpdated.setText(updated);


        }
    }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            int id = item.getItemId();

            switch (item.getItemId()){
                case R.id.action_save:
                    title = myMemoTitle.getText().toString().trim();
                    body = myMemoBody.getText().toString().trim();
                    if(title.equals("")){
                        Toast.makeText(
                                this,
                                "タイトルを入力してください",
                                Toast.LENGTH_LONG
                        ).show();
                    }else {
                        ContentValues values = new ContentValues();
                        values.put(MyConract.Memos.COLUMN_TITLE,title);
                        values.put(MyConract.Memos.COLUMN_BODY,body);
                        if (isNewMemo){
                            //insert
                            getContentResolver().insert(MyContentProvider.CONTENT_URI,values);

                        }else {
                            //updated
                            values.put(MyConract.Memos.COLUMN_UPDATED,
                                    android.text.format.DateFormat.format(
                                            "yyyy-MM-dd kk:mm:ss",
                                            new Date()
                                    ).toString()
                            );
                            Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI,memoId);
                            String selection = MyConract.Memos.COLUMN_ID + " =?";
                            String[] selectionArgs = new String[] {Long.toString(memoId)};
                            getContentResolver().update(
                                    uri,
                                    values,
                                    selection,
                                    selectionArgs
                            );
                        }
                        Intent intent = new Intent(EditActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    break;
                case R.id.action_delete:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("メモの削除");
                    alertDialog.setMessage("本当に削除しますか?");
                    alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI,memoId);
                            String selection = MyConract.Memos.COLUMN_ID + " = ?";
                            String selectionArgs[] = new String[]{ Long.toString(memoId)};
                            getContentResolver().delete(
                                    uri,
                                    selection,
                                    selectionArgs
                            );
                            Intent intent = new Intent(EditActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    });
                    alertDialog.show();
                    break;
            }

            return super.onOptionsItemSelected(item);
        }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
