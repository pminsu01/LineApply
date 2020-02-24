package com.mspark.myapplication.Activty;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mspark.myapplication.Adapter.ImageRecyclerViewAdapter;

import com.mspark.myapplication.CustomAlertDialog;
import com.mspark.myapplication.DataBaseHelper;
import com.mspark.myapplication.GetImageArrayConvert;
import com.mspark.myapplication.GetImageConvert;
import com.mspark.myapplication.GetURLImageAsyncTask;
import com.mspark.myapplication.ImageItemModel;
import com.mspark.myapplication.MemoItemModel;
import com.mspark.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class MemoDetailReadViewActivity extends Activity implements ImageRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "MemoReadViewActivity";


    /**
     * 변수 선언
     * <p>
     * 1. 입력 받는 변수
     * title : 입력 받는 메모장 제목
     * contents : 입력 받는 메모장 내용
     * saveButton : 메모 저장 버튼
     * imageSaveButton : 이미지 첨부 버튼
     * boolImageView : 이미지 삽입 / 취소 Flag
     */

    EditText title;
    EditText contents;
    TextView date;
    Button saveButton;
    Button imageSaveButton;
    Button removeButton;
    Boolean boolImageView = false;

    /**
     * Layout 및 view 관련
     * <p>
     * 1) imageLinearLayout : imageLinearLayout을 Visiable, Gone을 설정하기 위해서 따로 변수 설정함
     * 2) recyclerImageView : RecyclerVIew 사용
     * <p>
     * <p>
     * 객체 관련
     * <p>
     * 1) DataBaseHelp : sqlite를 쓰기 위한 선언
     * 2) imageItemModelList : RecyclerAdapter를 호출 하기 위한 이미지 List
     */

    LinearLayout imageLinearLayout;
    RecyclerView recyclerImageView; //RectclerView 선언
    DataBaseHelper dataBaseHelper = null;
    ArrayList<ImageItemModel> imageItemModelList = new ArrayList<ImageItemModel>(); //Image 리스트
    ArrayList<String> removeImageList = new ArrayList<String>(); //이미지 삭제 리스트
    ArrayList<String> removeCacheFileList = new ArrayList<String>();


    /**
     * 기타 선언들
     * <p>
     * 1) imageFilePath : Image파일 저장 위치를 관리하기 위한 변수
     * 2) DB 이용에 필요한 DB이름과 테이블 이름
     * 3) imageAbsolutePath : 앨범 및 URL 저장 관리를 위한 변수
     */
    private String imageFilePath;
    private String ApplicationName;
    public static final String DBName = "LineMemoPJ.db";
    public static final String TableName = "LineMemoTable";

    private String imageAbsolutePath;
    private Uri photoUri;
    /**
     * 해당 변수는 DB에서 불러온 값을 저장하기 위한 변수
     */
    int memoIndex;
    String memoTitle;
    String memoContents;
    String memoImage;
    String memoDate;

    /**
     * 해당 값은 DB Select 한 후 받은 값.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memodetailview);
        ApplicationName = getString(R.string.app_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        imageAbsolutePath = this.getFilesDir().getAbsolutePath();

        dataBaseHelper = new DataBaseHelper(this, DBName, null, 1);
        HashMap<String, String> selectDB = new HashMap<String, String>();
        imageLinearLayout = findViewById(R.id.image_layout); //imageview_layout


        title = findViewById(R.id.memo_title); // 메모 제목
        contents = findViewById(R.id.memo_contents); // 메모 컨텐츠(자세한 내용)
        date = findViewById(R.id.memo_date); //메모 작성 날짜
        saveButton = findViewById(R.id.memo_save); // 메모 저장 버튼
        removeButton = findViewById(R.id.memo_remove); // 메모 삭제 버튼
        imageSaveButton = findViewById(R.id.image_button); // 이미지 불러오기 버튼
        recyclerImageView = findViewById(R.id.image_listview); // 이미지 가로뷰


        saveButton.setText("수정");
        saveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMemo();

            }
        });

        //짝수번 클릭 시 Gone, 홀수번 클릭 시 Visible
        imageSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout;
                linearLayout = findViewById(R.id.image_call_layout);
                if (boolImageView == false) {
                    linearLayout.setVisibility(View.VISIBLE);
                    imageSaveButton.setText("이미지 삽입 취소");
                    boolImageView = true;
                } else {
                    linearLayout.setVisibility(View.GONE);
                    imageSaveButton.setText("이미지 삽입");
                    boolImageView = false;

                }
            }
        });

        /**
         * 삭제 버튼
         */
        removeButton.setVisibility(View.VISIBLE);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomAlertDialog customAlertDialog = new CustomAlertDialog((v.getContext()));
                customAlertDialog.customDialog("remove", memoIndex);


            }
        });

        imageSelect(); //Image 삽입관련 함수
        setMemoDetailView(); //DB를 불러와서 set해주는 함수


    }


    /**
     * ListViewActivity로 돌아가기.
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //saveMemo();
        removeCacheFile();
        Intent intent = new Intent(this, MemoListViewActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 2020.02.22 Erjuer01
     * - 메모 저장 Method
     */
    public void saveMemo() {

        //메모 저장 시간.
        String saveTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        GetImageArrayConvert getImageArrayConvert = new GetImageArrayConvert(this);

        MemoItemModel memoItemModel = new MemoItemModel();
        memoItemModel.set_id(memoIndex);
        memoItemModel.setMemoTitle(title.getText().toString());
        memoItemModel.setMemoContents(contents.getText().toString());


        String imgURLString = ""; //이미지List형태를 String으로 저장하기 위한 변수

        if (!imageItemModelList.isEmpty()) {
            imgURLString = getImageArrayConvert.ImageListToString(imageItemModelList, true);
        }
        memoItemModel.setMemoImage(imgURLString);
        memoItemModel.setMemoDate(saveTimeStamp);

        int result = dataBaseHelper.updateDataBase(memoItemModel); //DB 업데이트


        if (TextUtils.isEmpty(title.getText().toString())) {
            customDialog("titleEmpty", 0); // 제목이 없을 때 customDialog 실행

        } else if (result > 0) {
            removeCacheFile();
            Toast.makeText(this, "수정 되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MemoListViewActivity.class);
            startActivity(intent);
            finish();

        } else {
            removeCacheFile();
            Toast.makeText(this, "수정 실패하였습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }


        //실제 이미지 사진 삭제 함수 호출
        if (!removeImageList.isEmpty()) {
            removeImageFile();
        }


        removeCacheFile();

    }

    /**
     * 이미지 클릭 관련 함수
     * 버튼에 따라 카메라, 앨범, URL 사진 가져오는 메소드 실행
     */
    public void imageSelect() {


        ImageView camera = findViewById(R.id.camera_view);
        ImageView album = findViewById(R.id.album_view);
        ImageView link = findViewById(R.id.link_view);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraImage();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlbumImage();
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog("getURLImage", 0);
            }
        });
    }


    /**2020.02.24 Erjuer01
     * getCameraImage() : 카메라 실행
     * cache파일 저장으로 수정
     */
    private void getCameraImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.mspark.myapplication",
                        photoFile);



                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }


    /**
     * 2020.02.19 Erjuer01
     * - getAlbumImage() : 앨범 내부 사진 선택
     */
    private void getAlbumImage() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    /**
     * 2020.02.20 Erjuer01
     * - getAlbumImage() : 앨범 내부 사진 선택
     */

    private void getUrlImage(String tempGetImageURL) {


        try {
            String urlTimeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            ImageItemModel imageItemModel = new ImageItemModel();

            GetURLImageAsyncTask getURLImageAsyncTask = new GetURLImageAsyncTask(this);
            Bitmap bitmap = getURLImageAsyncTask.execute(tempGetImageURL).get();

            imageItemModel.setImageBitmap(bitmap);
            imageItemModel.setImageFileName(urlTimeStamp);
            setImageViewAdapter(imageItemModel);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    /**
     * 2020.02.20 Erjuer01
     * - getCamera(). getAlbumImage() intent호출에 따른 결과 값처리
     *
     * @param requestCode 카메라:0 , 데이터 내부 앨범:1
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap tempBitmap = null;
        ImageItemModel imageItemModel = new ImageItemModel();
        //getCamera()
        if (requestCode == 0 && resultCode == RESULT_OK) {


            try {

                tempBitmap =  MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),photoUri);

                String strPhotoURI = photoUri.toString();

                String[] strPhotoURITemp = strPhotoURI.split("cache/");
                String resultURI = strPhotoURITemp[1].substring(0,13);


                imageItemModel.setImageBitmap(tempBitmap);
                imageItemModel.setImageFileName(resultURI);
                setImageViewAdapter(imageItemModel);
                imageLinearLayout.setVisibility(View.VISIBLE);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //getAlbumImage
        else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String alBumTimeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            tempBitmap = BitmapFactory.decodeStream(in);

            imageItemModel.setImageFileName(alBumTimeStamp);
            imageItemModel.setImageBitmap(tempBitmap);
            setImageViewAdapter(imageItemModel);


        } else {

            Toast.makeText(this, "사진 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        //새로 고침
        Intent intent = new Intent(MemoDetailReadViewActivity.this, MemoDetailReadViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    /**
     * 2020.02.23 Erjuer01
     * - 카메라 실행 했을 때 임시 파일 저장소 Method
     * https://developer.android.com/training/camera/photobasics.html#java
     */
    public File createImageFile() throws IOException {

        String imageFileName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory() + "/Pictures");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        removeCacheFileList.add(imageAbsolutePath);

        return image;
    }


    /**
     * 2020.02.23 Erjuer01
     * - 이미지를 추가 했을 때 RecyclerViewAdapter 호출 하는 함수
     *
     * @param bitmap : Image를 Bitmap 변환, 이 단계 까지는 단순히 ImageList add, save가 아니다.
     */

    public void setImageViewAdapter(ImageItemModel imageItemModel) {

        recyclerImageView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerImageView.setLayoutManager(linearLayoutManager);
        imageItemModelList.add(imageItemModel);

        ImageRecyclerViewAdapter imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(imageItemModelList, this, this);
        recyclerImageView.setAdapter(imageRecyclerViewAdapter);
        imageLinearLayout.setVisibility(View.VISIBLE);

    }

    /**
     * 2020.02.23 Erjuer01
     * - 처음 엑티비티 실행 했을 때 RecyclerViewAdapter 호출 하는 함수
     *
     * @param readItemImageList : DB값에 갖고 있는 ImageItem정보 ArrayList
     */
    public void readImageViewAdapter(ArrayList<ImageItemModel> readItemImageList) {


        recyclerImageView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerImageView.setLayoutManager(linearLayoutManager);

        imageItemModelList = readItemImageList;

        ImageRecyclerViewAdapter imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(imageItemModelList, this, this);
        recyclerImageView.setAdapter(imageRecyclerViewAdapter);

    }

    /**
     * 2020.02.23 Erjuer01
     * - 삭제 했을 때 다시 RecyclerViewAdapter 새로고침하는 함수
     */
    public void refreshImageViewAdapter() {

        recyclerImageView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerImageView.setLayoutManager(linearLayoutManager);
        ImageRecyclerViewAdapter imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(this, this);
        imageRecyclerViewAdapter.notifyDataSetChanged();
        if (imageItemModelList.isEmpty()) {
            imageLinearLayout.setVisibility(View.GONE);
        }
    }


    /**
     * 2020.02.23 Erjuer01
     * customDialog if문 -> Swith문 변경
     * - Case 별로 Dialog 표시
     * 1) 이미지 URL 입력
     * 2) 메모 제목이 없을 때
     * 3) 사진 삭제 할 때
     *
     * @return
     */
    public void customDialog(String index, final int position) {


        final int removeImagePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (index) {
            case "getURLImage":
                final EditText editText = new EditText(this);

                builder.setTitle(ApplicationName);
                builder.setMessage("이미지 URL을 입력해주세요");
                builder.setView(editText);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String tempGetImageURL = editText.getText().toString();
                        getUrlImage(tempGetImageURL);

                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;
                    }
                });

                break;
            case "titleEmpty":
                builder.setTitle(ApplicationName);
                builder.setMessage("메모 제목을 작성하지 않았습니다. \n메모 작성을 취소하시겠습니까?");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeCacheFile();
                        finish();

                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;
                    }
                });
                break;

            case "removeImage":
                builder.setTitle(ApplicationName);
                builder.setMessage("해당 사진을 삭제하시겠습니까");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        imageItemModelList.remove(removeImagePosition);
                        Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        refreshImageViewAdapter();


                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;
                    }
                });
                break;


            default:
                break;


        }


        builder.show();

    }


    /**
     * 2020.02.23 Erjuer01
     * - Activty 이동시 DB값에 있는 데이터 바탕으로 HashMap 형태 return
     */
    public HashMap<String, String> selectDataBase() {

        HashMap<String, String> selectValue = new HashMap<String, String>();
        //dataBaseHelper = new DataBaseHelper(this, DBName, null, 1);

        memoIndex = getIntent().getIntExtra("_id", 1); //MemoListView에서 넘어 온 _id 값
        MemoItemModel memoItemModel = new MemoItemModel();
        memoItemModel.set_id(memoIndex);
        selectValue = dataBaseHelper.selectDataBase(memoItemModel);

        return selectValue;


    }


    /**
     * 2020.02.23 Erjuer01
     * - selectDataBase()를 실행하여 얻은 값으로 set
     */
    public void setMemoDetailView() {


        HashMap<String, String> selectDB = new HashMap<String, String>();
        GetImageArrayConvert getImageArrayConvert = new GetImageArrayConvert(this);
        selectDB = selectDataBase(); //DB 쿼리 조회

        memoTitle = selectDB.get("memoTitle");
        memoContents = selectDB.get("memoContents");
        memoImage = selectDB.get("memoImage");
        memoDate = selectDB.get("memoDate");

        Log.d(TAG, memoImage);
        if (memoImage.equals("")) {
            imageItemModelList.clear();
        } else {


            imageLinearLayout.setVisibility(View.VISIBLE);
            GetImageConvert getImageConvert = new GetImageConvert();
            imageItemModelList = getImageArrayConvert.ImageStringArrayToList(memoImage, this);
            readImageViewAdapter(imageItemModelList);


        }

        title.setText(memoTitle);
        contents.setText(memoContents);
        date.setText(memoDate);

    }

    /**
     * 2020.02.23 Erjuer01
     * - 이미지 리스트에서 이미지 삭제 후 saveMemo 함수 호출 시 실제 ImageFile 삭제
     */
    public void removeImageFile() {

        Iterator<String> itr = removeImageList.iterator();

        while (itr.hasNext()) {

            String realImagePath = imageAbsolutePath + "/" + itr.next();
            Log.d(TAG + "_re", realImagePath);
            File file = new File(realImagePath);
            file.delete();

        }

    }

    /**
     * 2020.02.23
     * - RecyclerViewAdapter onclick 호출
     * https://thepassion.tistory.com/300
     *
     * @param v
     * @param position
     */

    @Override
    public void onItemClick(View v, int position) {
        customDialog("removeImage", position);
    }

    public void removeCacheFile() {


        for (int i = 0; i < removeCacheFileList.size(); i++) {


            File file = new File(removeCacheFileList.get(i));
            if (file.exists()) {
                Log.d(TAG, "CacheImage Remove");
                file.delete();
            }


        }

    }


}
