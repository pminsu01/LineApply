package com.mspark.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.concurrent.ExecutionException;

public class GetURLImageAsyncTask extends AsyncTask<String, Object, Bitmap> {

    Context mcontext;
    Bitmap bitmap = null;


    public GetURLImageAsyncTask(Context context) {
        this.mcontext = context;
    }

    @SuppressLint("CheckResult")
    @Override
    protected Bitmap doInBackground(String... str) {

        String getImageURL = str[0]; //String 0 번째 Parameter값으로  url 넘어 갈 것이기 때문에


        try {
            bitmap = Glide
                    .with(mcontext)
                    .asBitmap()
                    .load(getImageURL)
                    .submit()
                    .get();

        } catch (ExecutionException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap ==null)  {Toast.makeText(mcontext,"이미지 형식이 아닙니다.",Toast.LENGTH_SHORT).show(); return;}
        super.onPostExecute(bitmap);

    }


}
