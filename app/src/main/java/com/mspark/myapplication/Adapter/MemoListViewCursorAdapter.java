package com.mspark.myapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mspark.myapplication.Activty.MemoDetailReadViewActivity;

import com.mspark.myapplication.CustomAlertDialog;
import com.mspark.myapplication.GetImageConvert;
import com.mspark.myapplication.R;

public class MemoListViewCursorAdapter extends CursorAdapter {

    Context mcontext;
    final String TAG ="MeMoListViewAdapter";

    public MemoListViewCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public Bitmap findThumbNailImage(Context context, String imgURL){

        this.mcontext =context;
        GetImageConvert getImageConvert = new GetImageConvert();
        String[] tempThumbNail= imgURL.split(",");
        imgURL = mcontext.getFilesDir().getAbsolutePath()+"/"+tempThumbNail[0]; //첫번째
        Bitmap bitmap= getImageConvert.JPGSaveBitmap(mcontext,imgURL);

        return bitmap;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.activtiy_memolistviewitem, viewGroup, false);
        return v;
}

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView memoListTitle = view.findViewById(R.id.memo_list_item_title);
        TextView memoListContents = view.findViewById(R.id.memo_list_item_contents);
        TextView memoListDate = view.findViewById(R.id.memo_list_item_date);
        ImageView memoListImage = view.findViewById(R.id.memo_list_item_thumbnail);

        final int memoIndex = cursor.getInt(cursor.getColumnIndex("_id"));
        String memoTitle = cursor.getString(cursor.getColumnIndex("memoTitle"));
        String memoContents = cursor.getString(cursor.getColumnIndex("memoContents"));
        String memoDate = cursor.getString(cursor.getColumnIndex("memoDate"));
        String memoImage = cursor.getString(cursor.getColumnIndex("memoImage"));

        Log.d(TAG, memoTitle);
        Log.d(TAG, memoContents);
        Log.d(TAG, memoDate);
        Log.d(TAG,memoImage);
        if(memoImage !=null) {
            Bitmap thumbNailBitmap = findThumbNailImage(context, memoImage);
            memoListImage.setImageBitmap(thumbNailBitmap);
        }

        //10개 글자 넘었을 때
        if (memoContents.length() > 10) {
            memoContents = memoContents.substring(0, 10) + ".......";
        }

        memoListTitle.setText(memoTitle);
        memoListContents.setText(memoContents);
        memoListDate.setText(memoDate);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MemoDetailReadViewActivity.class);
                intent.putExtra("_id", memoIndex);
                v.getContext().startActivity(intent);
                ((Activity)context).finish();



            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                CustomAlertDialog customAlertDialog = new CustomAlertDialog(context);
                customAlertDialog.customDialog("remove",memoIndex);
                return true;
            }
        });

    }
}
