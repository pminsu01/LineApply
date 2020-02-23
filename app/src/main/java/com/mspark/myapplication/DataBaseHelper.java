package com.mspark.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.Toast;

import com.mspark.myapplication.Activty.MemoDetailViewActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {


    /**
     * Table 이름 : LineMemoTable
     * Columm 1: memoIndex
     * Columm 2: memoTitle
     * Columm 3: memoContents
     * Columm 4: memoImage
     * Columm 5: memoDate
     */
    public static final String Index = "_id";
    public static final String Title = "memoTitle";
    public static final String Contents = "memoContents";
    public static final String Images = "memoImage";
    public static final String Date = "memoDate";
    public static final String TableName = "LineMemoTable";
    public static final String DBName = "LineMemoPJ.db";
    private static final String TAG = "DataBaseHelper";

    SQLiteDatabase sqLiteDatabase;
    public Context context;

    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    /**
     * @param sqLiteDatabase getWritableDataBase(), getReadableDataBase() 실행할 때 onCreate 실행
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query = "";

        query = "CREATE TABLE if not exists "
                + TableName + "(" + Index + " INTEGER PRIMARY KEY AUTOINCREMENT," + Title + " TEXT," + Contents + " TEXT," + Images + " TEXT," + Date + " TEXT)";
        sqLiteDatabase.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * ListView를 뿌려주기 위한 함수
     * @return
     */
    public Cursor allSelectDataBase() {

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String allSelectQuery = String.format("select * from %s ORDER BY _id DESC", TableName);
        Cursor cursor = sqLiteDatabase.rawQuery(allSelectQuery, null);

        return cursor;
    }


    /**
     * memoDate를 위한 함수
     * @return
     */
    public String nowDate() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = simpleDateFormat.format(date);

        return formatDate;
    }

    /**
     * DB 삽입
     * @param memoItemModel
     * @return
     */
    public String insertDataBase(MemoItemModel memoItemModel) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String memoTitle = memoItemModel.getMemoTitle();
        String memoContents = memoItemModel.getMemoContents();
        String memoImages = memoItemModel.getMemoImage();
        String memoDate = nowDate();

        if (TextUtils.isEmpty(memoTitle)) {
            return "titleEmpty";
        }

        contentValues.put(Title, memoTitle);
        contentValues.put(Contents, memoContents);
        contentValues.put(Images, memoImages);
        contentValues.put(Date, memoDate);

        Log.d("DataBaseHelper", nowDate());

        Log.d(TAG + "_I", memoTitle);
        Log.d(TAG + "_I", memoContents);
        Log.d(TAG + "_I", memoImages);
        Log.d(TAG + "_I", memoDate);


        long result = sqLiteDatabase.insert(TableName, null, contentValues);

        if (result == -1) {
            return "false";
        } else {
            return "true";
        }

    }

    /**
     * DB 삭제
     * @param index 에 따라서 삭제한다. (DetailView도 이를 활용한다.)
     * @return
     */
    public boolean deleteDataBase(String index) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int result = sqLiteDatabase.delete(TableName, "_id=?", new String[]{index});


        if (result == -1) {

            return false;
        } else {
            return true;
        }

    }

    /**
     * DB 업데이트
     * @param memoItemModel
     * @return
     */
    public int updateDataBase(MemoItemModel memoItemModel) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        int memoIndex = memoItemModel.get_id();
        String memoTitle = memoItemModel.getMemoTitle();
        String memoContents = memoItemModel.getMemoContents();
        String memoImages = memoItemModel.getMemoImage();
        String memoDate = memoItemModel.getMemoDate();

        contentValues.put(Title, memoTitle);
        contentValues.put(Contents, memoContents);
        contentValues.put(Images, memoImages);
        contentValues.put(Date,memoDate);

        Log.d(TAG + "_U", String.valueOf(memoIndex));
        Log.d(TAG + "_U", memoTitle);
        Log.d(TAG + "_U", memoContents);
        Log.d(TAG + "_U", memoImages);
        Log.d(TAG + "_U", memoDate);

        int result = sqLiteDatabase.update(TableName, contentValues, "_id =?", new String[]{String.valueOf(memoIndex)});

        return result;
    }

    /**
     * DB조회
     * @param memoItemModel
     * @return
     */
    @SuppressLint("LogConditional")
    public HashMap<String, String> selectDataBase(MemoItemModel memoItemModel) {

        HashMap<String, String> selectValue = new HashMap<String, String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        int memoIndex = memoItemModel.get_id();

        contentValues.put("_id", memoIndex);

        String query = "SELECT * FROM "
                + TableName + " WHERE " + Index + "=" + String.valueOf(memoIndex);

        Log.d(TAG, query);
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToLast();

        Log.d(TAG, cursor.getColumnName(0));
        Log.d(TAG, cursor.getColumnName(1));
        Log.d(TAG, cursor.getColumnName(2));
        Log.d(TAG, cursor.getColumnName(3));
        Log.d(TAG, cursor.getColumnName(4));

        Log.d(TAG, "================================================");

        int selectMemoIndex = cursor.getInt(cursor.getColumnIndex("_id"));
        String selectMemoTitle = cursor.getString(cursor.getColumnIndex("memoTitle"));
        String selectMemoContents = cursor.getString(cursor.getColumnIndex("memoContents"));
        String selectMemoImage = cursor.getString(cursor.getColumnIndex("memoImage"));
        String selectMemoDate = cursor.getString(cursor.getColumnIndex("memoDate"));

        Log.d(TAG, String.valueOf(selectMemoIndex));
        Log.d(TAG, selectMemoTitle);
        Log.d(TAG, selectMemoContents);
        Log.d(TAG, selectMemoImage);
        Log.d(TAG, selectMemoDate);

        if(selectMemoImage == null) selectMemoImage = "";

        selectValue.put("memoIndex", String.valueOf(selectMemoIndex));
        selectValue.put("memoTitle", selectMemoTitle);
        selectValue.put("memoContents", selectMemoContents);
        selectValue.put("memoImage", selectMemoImage);
        selectValue.put("memoDate", selectMemoDate);


        return selectValue;
    }


}
