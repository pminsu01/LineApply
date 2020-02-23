package com.mspark.myapplication;

import android.graphics.Bitmap;

public class ImageItemModel {

    private String imageFileName; //Timestamp로 생각
    private Bitmap imageBitmap; //image biotmap

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
