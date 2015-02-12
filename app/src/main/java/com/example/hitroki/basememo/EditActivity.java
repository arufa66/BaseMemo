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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class EditActivity extends ActionBarActivity {

    private boolean isNewMemo = true;
    private long memoId;
    private EditText myMemoTitle;
    private EditText myMemoBody;
    private TextView myMemoUpdated;
    private String title = "";
    private String body = "";
    private String updated = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        myMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        myMemoBody = (EditText) findViewById(R.id.myMemoBody);
        myMemoUpdated = (TextView) findViewById(R.id.Updated);

        Intent intent = getIntent();
        memoId = intent.getLongExtra(MainActivity.EXTRA_MYID, 0L);
        isNewMemo = memoId == 0L ? true : false;

        if (isNewMemo) {
            //new memo
            setTitle("New Memo");
        } else {
            // edit memo
            setTitle("Edit Memo");
            Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
            String[] projection = new String[]{
                    MyConract.Memos.COLUMN_TITLE,
                    MyConract.Memos.COLUMN_BODY,
                    MyConract.Memos.COLUMN_UPDATED

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
                                "Please enter title",
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
                    alertDialog.setTitle("Delete Memo");
                    alertDialog.setMessage("Are you sure to delete this memo?");
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

}
