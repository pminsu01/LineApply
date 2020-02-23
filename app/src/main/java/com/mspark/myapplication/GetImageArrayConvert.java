package com.mspark.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.util.LocaleData;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class GetImageArrayConvert {

    private static final String TAG = "GetImageArrayConvert";
    /**
     * 사진 저장 방법
     *
     * 1) Bitmap을 JPG 파일로 저장한다.
     * 2) 각각의 JPG 파일들의 이름들을 ArrayList<String>으로 저장한다.
     * 3) ArrayList<String> 값을 이용하여 String[]으로 만든다
     * 4) String[]  값을 String으로 만든다.
     * 5) DB에 그 String값을 저장한다.
     *
     *
     * ImageListToString -> StringArrayToString -> ImageListToString
     *
     *
     *
     * 사진 불러오기 방법
     * 1) DB에 있는 String값을 불러온다
     * 2) String값을 ',' 기준으로 String[]로 변환 그리고 String[]값을 ArrayList<String> 값으로 저장 후 Activity에 Return한다. (ImageStringArrayToList)
     * 3) Activity는 해당 JPG 저장 위치를 기억하여 이를 Bitmap으로 변환
     * 4) Bitmap -> setImageview하여 보여준다.
     *
     *
     */
    Context mcontext;
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    public GetImageArrayConvert(Context context) {

        this.mcontext = context;

    }

    /**
     * 이미지 ArrayList를 String값으로 저장하기 위한 Converting 함수.
     *
     * ArrayList<String> -> String[]
     *
     * @param tempimageList
     * @return
     */
    public String ImageListToString(ArrayList<ImageItemModel> tempImageItemModelList, boolean DetailView) {


        final String TAG ="GetImageArrayConvert";
        Iterator<ImageItemModel> itr = tempImageItemModelList.iterator();
        GetImageConvert getImageConvert = new GetImageConvert();
        int arraySize = tempImageItemModelList.size();

        String[] imageArray = new String[arraySize];
        int i = 0;

        while (itr.hasNext()) {

            ImageItemModel tempImageItemModel = itr.next();
            Bitmap bitmap = tempImageItemModel.getImageBitmap();
            String imageFileName = tempImageItemModel.getImageFileName();

            Log.d(TAG,imageFileName);
                try {
                    String tempURL = getImageConvert.BitmapSaveToJPG(mcontext, bitmap, imageFileName, DetailView);
                    imageArray[i++] = tempURL;

                } catch (IOException e) {
                e.printStackTrace();
                break;
            }


        }

        String result = StringArrayToString(imageArray);

        //filename : timestamp 값이 result 로 반환된다.
        return result;

    }


    /**
     * String 배열을 String값으로 변환하는 함수
     * (String[] -> String) ex) a,b
     * @param tempArray
     * @return
     */

    public String StringArrayToString(String[] tempArray) {


        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tempArray.length; i++) {
            stringBuilder.append(tempArray[i]);
            if (i == tempArray.length - 1) break;
            stringBuilder.append(",");
        }


        return stringBuilder.toString();

    }

    /**
     * 이미지 String을 배열로 바꾸고 Bitmap으로 Converting하고
     * ArrayList에 담아 return 한다.
     * @param imagepath
     * @return
     */
    @SuppressLint("LogConditional")
    public ArrayList<ImageItemModel> ImageStringArrayToList(String imagepath, Context context) {

        String imageDirPath = context.getFilesDir().getAbsolutePath();

        GetImageConvert getImageConvert = new GetImageConvert();


        ArrayList<ImageItemModel> getImageItemList = new ArrayList<ImageItemModel>();


        String[] arrayImageList;
        arrayImageList = imagepath.split(",");
        for (int i = 0; i < arrayImageList.length; i++) {

            ImageItemModel imageItemModel = new ImageItemModel();
            String tempFileName = arrayImageList[i];
            Bitmap tempBitmap = getImageConvert.JPGSaveBitmap(mcontext, imageDirPath+"/"+arrayImageList[i]);

            imageItemModel.setImageFileName(tempFileName);
            imageItemModel.setImageBitmap(tempBitmap);
            getImageItemList.add(imageItemModel);

        }




        return getImageItemList;
    }


}
