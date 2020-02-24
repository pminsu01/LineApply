package com.mspark.myapplication;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class GetImageConvert {


    /**
     *
     * 20.02.24 Erjuer01
     * - 화면 Rotate 로직 추가.
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

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
