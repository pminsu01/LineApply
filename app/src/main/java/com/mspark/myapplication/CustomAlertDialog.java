package com.mspark.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;

import com.mspark.myapplication.Activty.MemoDetailViewActivity;
import com.mspark.myapplication.Activty.MemoListViewActivity;

public class CustomAlertDialog {

    public static final String DBName = "LineMemoPJ.db";
    Context mcontext;


    public CustomAlertDialog(Context context) {

        this.mcontext = context;

    }


    public void customDialog(String index, final int memoIndex) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        if (index.equals("remove")) {
            builder.setTitle("MyApplication");
            builder.setMessage("메모를 삭제 하시겠습니까");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    DataBaseHelper dataBaseHelper = new DataBaseHelper(mcontext, DBName, null, 1);
                    dataBaseHelper.deleteDataBase(String.valueOf(memoIndex));
                    Intent intent = new Intent(mcontext, MemoListViewActivity.class);

                    ((Activity) mcontext).finish(); //새로 고침
                    mcontext.startActivity(intent);

                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    return;
                }
            });


        }


        builder.show();

    }


/*
    public boolean removeImageDialog() {

        builder.setTitle("MyApplication");
        builder.setMessage("이미지를 삭제하시겠습니까");


        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return false;
            }
        });

        builder.show();

    }*/

}
