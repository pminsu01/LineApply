package com.mspark.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class GetImageConvert {


    /**
     *
     * @param context
     * @param bitmap
     * @param fileName
     * @param DetailView 수정 : false, 생성:true
     * @return
     * @throws IOException
     */
    public String BitmapSaveToJPG(Context context, Bitmap bitmap, String fileName, boolean DetailView) throws IOException {


        if(DetailView == false){
            fileName = fileName + ".jpg";
        }

        String dirPath = context.getFilesDir().getAbsolutePath();

        String fileSaveDir = dirPath + "/" + fileName;
        File file = new File(fileSaveDir);

        if (file.isDirectory()) {
            file.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(fileSaveDir);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.close();

        return fileName;


    }

    public Bitmap JPGSaveBitmap(Context context, String imgFilePath) {

        Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
        return bitmap;
    }
}
