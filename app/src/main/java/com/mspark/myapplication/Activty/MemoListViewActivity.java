package com.mspark.myapplication.Activty;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mspark.myapplication.Adapter.MemoListViewCursorAdapter;
import com.mspark.myapplication.DataBaseHelper;
import com.mspark.myapplication.R;


/**
 * 변수 표기법
 * 1. 파스칼 표기법 ex) ParkMinsu : class에 활용하기.
 * 2. 언더스코어 표기법 ex) park_min_su : layout에 활용하기.
 * 3. 케멀 표기법 : ex)parkMinsu : Method 또는 변수 에 활용하기.
 * 4. 헝가리언 표기법 : ex)int ParkMinSu -> iParkMinSu
 *
 *
 */

public class MemoListViewActivity extends Activity {

    final static String TAG = "MemoListViewActivity";


    public static final String DBName = "LineMemoPJ.db";

    DataBaseHelper dataBaseHelper;
    ListView listView;
    FloatingActionButton floatingActionButton;
    MemoListViewCursorAdapter memoListViewCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memolistview);

        setMemoListView(); // ListView 출력

        /**
         * FloatingAction 추가
         */
        floatingActionButton = findViewById(R.id.add_memo);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MemoDetailViewActivity.class);
                startActivity(intent);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if(grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] !=PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"카메라 및 저장공간에 대한 권한 허용이 필요합니다. \n앱을 종료합니다.",Toast.LENGTH_LONG).show();
                finish();
            }

            else{
                Toast.makeText(this,"카메라 및 저장공간에 대한 권한을 허용하였습니다.",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setMemoListView() {

        Cursor cursor;
        dataBaseHelper = new DataBaseHelper(this, DBName, null, 1);
        cursor = dataBaseHelper.allSelectDataBase();

        if (cursor.getCount() == 0) return;

        memoListViewCursorAdapter = new MemoListViewCursorAdapter(this, cursor, true);
        listView = findViewById(R.id.memo_list_view);
        listView.setAdapter(memoListViewCursorAdapter);


    }

}
