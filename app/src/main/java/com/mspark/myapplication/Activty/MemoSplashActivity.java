package com.mspark.myapplication.Activty;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.mspark.myapplication.R;

public class MemoSplashActivity extends Activity {
    private static final String TAG = "MemoSplashActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activty_splash);


        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 3000);


    }

    private class splashHandler implements Runnable {

        @Override
        public void run() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getApplication(), MemoListViewActivity.class);
                    startActivity(intent);
                    MemoSplashActivity.this.finish();
                } else {
                    ActivityCompat.requestPermissions(MemoSplashActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
        }

        Intent intent = new Intent(getApplication(), MemoListViewActivity.class);
        startActivity(intent);
            MemoSplashActivity.this.finish();

        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "카메라 및 저장공간에 대한 권한 허용이 필요합니다. \n앱을 종료합니다.", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Intent intent = new Intent(getApplication(), MemoListViewActivity.class);
                startActivity(intent);
                MemoSplashActivity.this.finish();

                Toast.makeText(this, "카메라 및 저장공간에 대한 권한을 허용하였습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
