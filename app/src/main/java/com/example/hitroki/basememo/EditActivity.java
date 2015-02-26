package com.example.hitroki.basememo;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class EditActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private boolean isNewMemo = true;
    private long memoId;
    private Spinner myCategorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private EditText myMemoTitle;
    private EditText myMemoBody;
    private TextView myMemoUpdated;
    private String title = "";
    private String body = "";
    private String updated = "";
    private String category = "";
    private String addTag = "新しいカテゴリを作成";
    private EditText editView;
    private Button positiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        myMemoTitle = (EditText) findViewById(R.id.myMemoTitle);
        myMemoBody = (EditText) findViewById(R.id.myMemoBody);
        myMemoUpdated = (TextView) findViewById(R.id.Updated);
        myCategorySpinner = (Spinner)findViewById(R.id.category);


        Intent intent = getIntent();
        //MainActivityから送られてきたidを取り出す。
        memoId = intent.getLongExtra(MainActivity.EXTRA_MYID, 0L);
        isNewMemo = memoId == 0L ? true : false;


        categoryAdapter = Category.setCategoryAdapter(this);
        categoryAdapter.add(addTag);
        myCategorySpinner.setAdapter(categoryAdapter);
        myCategorySpinner.setOnItemSelectedListener(this);


        if (isNewMemo) {
            //新しいメモの場合
            setTitle("新しいメモ");
        } else {
            // メモの編集の場合
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
                category = cursor.getString(cursor.getColumnIndex(MyConract.Memos.COLUMN_CATEGORY));
            }
            cursor.close();
            myMemoTitle.setText(title);
            myMemoBody.setText(body);
            myMemoUpdated.setText(updated);
            Category.setSpinnerSelection(myCategorySpinner, category);



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
                //タイトルが入力されていなかった場合、トーストを出す。
                if(title.equals("")){
                    Toast.makeText(
                            this,
                            "タイトルを入力してください",
                            Toast.LENGTH_LONG
                    ).show();
                    //メモの追加処理
                }else {
                    ContentValues values = new ContentValues();
                    values.put(MyConract.Memos.COLUMN_TITLE,title);
                    values.put(MyConract.Memos.COLUMN_BODY,body);
                    values.put(MyConract.Memos.COLUMN_CATEGORY,category);
                    if (isNewMemo){
                        // //メモの追加処理
                        getContentResolver().insert(MyContentProvider.CONTENT_URI,values);

                    }else {
                        //メモの更新処理
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
            //削除メニューボタンがクリックされた場合の処理
            case R.id.action_delete:
                //削除するかの確認のダイアログ生成。
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("メモの削除");
                alertDialog.setMessage("本当に削除しますか?");

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    //OKボタンをクリックした場合の処理、データベースから対象のメモが削除される
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, memoId);
                        String selection = MyConract.Memos.COLUMN_ID + " = ?";
                        String selectionArgs[] = new String[]{Long.toString(memoId)};
                        getContentResolver().delete(
                                uri,
                                selection,
                                selectionArgs
                        );
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                });
                //キャンセルボタンをクリックした場合の処理
                alertDialog.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                //ダイアログ表示中にほかの場所をクリックしても挙動しない。
                alertDialog.setCancelable(false);
                //ダイアログ表示
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
        //「新しいカテゴリを作成」を選択した場合の処理
        if(position == Category.getSpinnerPosition(myCategorySpinner, addTag)){
            editView = new EditText(EditActivity.this);
            //
            editView.setHint("ここに新しいカテゴリ名を入力してください。");
            InputFilter[] _inputFilter = new InputFilter[1];
            _inputFilter[0] = new InputFilter.LengthFilter(15);
            editView.setFilters(_inputFilter);
                    //カテゴリ追加用ダイアログの生成
            AlertDialog alertDialog = new AlertDialog.Builder(EditActivity.this)
                    //アイコンの指定、android側がデフォルト用意しているものを使用
                    .setIcon(android.R.drawable.ic_dialog_info)
                    //タイトル指定
                    .setTitle("新しいカテゴリの登録")
                            //setViewにてeditViewを設定します。
                    .setView(editView)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        //OKボタンをクリックした場合の挙動
                        public void onClick(DialogInterface dialog, int whichButton) {

                            //trimメソッドで空白を削除
                            category = editView.getText().toString().trim();

                            //新しいカテゴリを登録
                            categoryAdapter.add(category);
                            //
                            Category.setSpinnerSelection(myCategorySpinner, category);


                        }
                    }) .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Category.setSpinnerSelection(myCategorySpinner, category);
                        }
                    }).setCancelable(false)
                    .create();
            alertDialog.show();
            //alertDialogのOKボタンを取得
            positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            //positiveButtonはデフォルトで無効。
            positiveButton.setEnabled(false);

            //TextWatcherでリアルタイムにEditTextの挙動が受け取れる。
            editView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //ダイアログのEditTextに何も入力されていない場合、
                    //positiveButtonを無効にする処理
                    if(s.toString().equals("")&&s.toString().equals(addTag)){
                        positiveButton.setEnabled(false);
                    }else{
                        positiveButton.setEnabled(true);
                    }
                }
            });





        }else {
            //選ばれたカテゴリ名を取得。
            category =(String) myCategorySpinner.getItemAtPosition(position);
        }
    }



   //スピナーで何も選択しなかった際の処理、
   // 今回は必要なし、
   //OnItemSelectedListenerインターフェイスのオーバーライド
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
