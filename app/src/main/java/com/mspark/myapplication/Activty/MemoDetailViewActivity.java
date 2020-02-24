package com.mspark.myapplication.Activty;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.LocaleData;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mspark.myapplication.Adapter.ImageRecyclerViewAdapter;
import com.mspark.myapplication.DataBaseHelper;
import com.mspark.myapplication.GetImageArrayConvert;
import com.mspark.myapplication.GetImageConvert;
import com.mspark.myapplication.GetURLImageAsyncTask;
import com.mspark.myapplication.ImageItemModel;
import com.mspark.myapplication.MemoItemModel;
import com.mspark.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.concurrent.ExecutionException;

public class MemoDetailViewActivity extends Activity implements ImageRecyclerViewAdapter.OnItemClickListener {

    private static final String TAG = "MemoDetailViewActivity";
    DataBaseHelper dataBaseHelper = null;
    ArrayList<ImageItemModel> imageItemModelList = new ArrayList<ImageItemModel>();
    ArrayList<String> removeCacheFileList = new ArrayList<String>();
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
    Button saveButton;
    Button imageSaveButton;
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

    /**
     * 기타 선언들
     * <p>
     * 1) imageFilePath : Image파일 저장 위치를 관리하기 위한 변수
     * 2) DB 이용에 필요한 DB이름과 테이블 이름
     */
    private String imageFilePath;
    private File imageAbsolutePath;
    private String ApplicationName;
    public static final String DBName = "LineMemoPJ.db";
    public static final String TableName = "LineMemoTable";

    //카메라 활용을 위한 phtoUri값
    private Uri photoUri;
    private String phoUriStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memodetailview);
        imageAbsolutePath = this.getFilesDir().getAbsoluteFile();
        ApplicationName = getString(R.string.app_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        title = findViewById(R.id.memo_title); // 메모 제목
        contents = findViewById(R.id.memo_contents); // 메모 컨텐츠(자세한 내용)
        saveButton = findViewById(R.id.memo_save); // 메모 저장 버튼
        imageSaveButton = findViewById(R.id.image_button); // 이미지 불러오기 버튼
        recyclerImageView = findViewById(R.id.image_listview); // 이미지 가로뷰
        imageLinearLayout = findViewById(R.id.image_layout); // imageview_layout

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

        imageSelect(); //Image 삽입


    }


    /**
     * Back 버튼 눌렀을 때에도 저장.
     */
    @Override
    public void onBackPressed() {

        saveMemo();

    }

    /**
     * 2020.02.14 : EdiText에 있는 값 불러와서 DB 저장
     * MemoListViewActivity에 onResume을 불러와서 새로고침을 진행할 수 있다.
     * 2020.02.16 : 추가해야할 사항 : 이미지를 BItmap으로 저장한다.
     * https://flystone.tistory.com/146
     */
    public void saveMemo() {
        String saveTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        dataBaseHelper = new DataBaseHelper(this, DBName, null, 1);
        GetImageArrayConvert getImageArrayConvert = new GetImageArrayConvert(this);

        MemoItemModel memoItemModel = new MemoItemModel();
        memoItemModel.setMemoTitle(title.getText().toString());
        memoItemModel.setMemoContents(contents.getText().toString());


        //Bitmap과 filename 같이 전달.
        String imgURLString = getImageArrayConvert.ImageListToString(imageItemModelList, false);
        memoItemModel.setMemoImage(imgURLString);
        memoItemModel.setMemoDate(saveTimeStamp);


        String result = "false";


        result = dataBaseHelper.insertDataBase(memoItemModel); //DB 삽입
        Log.d("MemoDetailViewActivity", result);


        if (result == "true") {
            Toast.makeText(this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();
            removeCacheFile();
            Intent intent = new Intent(this, MemoListViewActivity.class);
            startActivity(intent);
            finish();

        } else if (result == "titleEmpty") {
            customDialog("titleEmpty", 0);
        } else {
            removeCacheFile();
            Toast.makeText(this, "저장에 실패 하였습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    /**
     * 이미지 클릭 관련 함수
     * 버튼에 따라 카메라, 앨범, URL 사진 가져오는 메소드 실행
     */
    public void imageSelect() {


        final ImageView camera = findViewById(R.id.camera_view);
        final TextView cameraText = findViewById(R.id.text_camera);
        final ImageView album = findViewById(R.id.album_view);
        final TextView albumText = findViewById(R.id.text_album);
        final ImageView link = findViewById(R.id.link_view);
        final TextView linkText = findViewById(R.id.text_link);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraImage();

            }
        });
        cameraText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCameraImage();
            }
        });

        albumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlbumImage();
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

        linkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog("getURLImage", 0);
            }
        });
    }


    /**
     * 2020.02.23 Erjuer01
     * - getCameraImage() : 사진 카메라
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
     * getUrlImage를 실행시켜서 받은 다음에 이 비트맵을 MemoActivity에 반환한다.
     */

    private void getUrlImage(String tempGetImageURL) {

        try {

            ImageItemModel imageItemModel = new ImageItemModel();
            String urlTimeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

            GetURLImageAsyncTask getURLImageAsyncTask = new GetURLImageAsyncTask(this); //AsncTask로 이미지 불러오기
            Bitmap bitmap = getURLImageAsyncTask.execute(tempGetImageURL).get();

            imageItemModel.setImageBitmap(bitmap);
            imageItemModel.setImageFileName(urlTimeStamp);

            setImageViewAdapter(imageItemModel);

            imageLinearLayout.setVisibility(View.VISIBLE);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     * 2020.02.20 Erjuer01
     * - getCamera(). getAlbumImage() intent호출에 따른 결과 값처리
     * <p>
     * 2020.02.24
     * - getCamera() 버그 수정 및 cache 삭제 로그 추가
     * - 카메라 촬영시 돌아간 사진 다시 바꿔주는 로직 추가.
     * https://raon-studio.tistory.com/6
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

        if (requestCode == 0 && resultCode == RESULT_OK) {

            try {

                tempBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoUri);

                /**
                 *  원래 이미지로 만들기
                 *  exifinterface : 이미지가 갖고 있는 정보의 집합 클래스
                 */
                int exifOrientation;
                int exifDegree;

                GetImageConvert getImageConvert = new GetImageConvert();
                ExifInterface exif = new ExifInterface(imageFilePath);


                if (exif != null) {
                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    exifDegree = getImageConvert.exifOrientationToDegrees(exifOrientation);
                } else {
                    exifDegree = 0;
                }


                String resultStr = photoUri.toString();
                String[] strTemp = resultStr.split("cache/");
                String Strjpg = strTemp[1];
                Strjpg = Strjpg.substring(0, 13);


                imageItemModel.setImageBitmap(getImageConvert.rotate(tempBitmap, exifDegree));
                imageItemModel.setImageFileName(Strjpg);
                setImageViewAdapter(imageItemModel);
                imageLinearLayout.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //getAlbumImage
        else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            tempBitmap = BitmapFactory.decodeStream(in);


            imageItemModel.setImageBitmap(tempBitmap);
            String alBumTimeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            imageItemModel.setImageFileName(alBumTimeStamp);
            setImageViewAdapter(imageItemModel);


            imageLinearLayout.setVisibility(View.VISIBLE);


        } else {

            Toast.makeText(this, "사진 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MemoDetailViewActivity.this, MemoDetailViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    /**
     * 2020.02.23 Erjuer01
     * - 카메라 실행 했을 때 임시 파일 저장소 Method
     * https://developer.android.com/training/camera/photobasics.html#java
     */
    public File createImageFile() throws IOException {

        String cameraImageFileName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File storageDir = this.getCacheDir();
        File image = null;

        image = File.createTempFile(cameraImageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        removeCacheFileList.add(imageFilePath);
        return image;

    }

    /*
     */


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

    @Override
    public void onItemClick(View v, int position) {
        customDialog("removeImage", position);
    }


    /**
     * 2020.02.24 Erjuer01
     * getCamera이후 Cache 파일 삭제를 위한 함수
     */

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
