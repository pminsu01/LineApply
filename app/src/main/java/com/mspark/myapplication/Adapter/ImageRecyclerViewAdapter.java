/**
 * 해당 Adapter는 이미지를 가로뷰로 하기 위해서 이용한다.
 * https://thdev.tech/androiddev/2016/11/01/Android-RecyclerView-intro/
 */

package com.mspark.myapplication.Adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mspark.myapplication.Activty.MemoDetailReadViewActivity;
import com.mspark.myapplication.CustomAlertDialog;
import com.mspark.myapplication.ImageItemModel;
import com.mspark.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * onCreateViewHolder(ViewGroup parent, int viewType)	viewType 형태의 아이템 뷰를 위한 뷰홀더 객체 생성.
 * onBindViewHolder(ViewHolder holder, int position)	position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
 * getItemCount()	전체 아이템 갯수 리턴.
 */

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.MyViewHolder> {


    private static final String TAG = "ImageViewAdapter";
    public ArrayList<ImageItemModel> imageItemList = new ArrayList<ImageItemModel>(); //Image 리스트
    private Context mcontext;
    private View.OnClickListener onClickListener;
    private RecyclerView recyclerView;
    private OnItemClickListener mListner = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void OnItemClickListener(OnItemClickListener listener) {
        this.mListner = listener;
    }


    /**
     * 클릭 시 사용 할 것.
     * public ImageViewAdapter(Context context,View.OnClickListener onclicklistener){
     * this.mcontext = context;
     * this.onClickListener = onclicklistener;
     * <p>
     * }
     */
/*
    public ImageRecyclerViewAdapter(Bitmap bitmap) {
        imageBitmapList.add(bitmap);
    }*/
    public ImageRecyclerViewAdapter(ArrayList<ImageItemModel> imageItemModel, Context context, OnItemClickListener listener) {
        this.imageItemList = imageItemModel;
        this.mListner = listener;
        this.mcontext = context;
    }


    public ImageRecyclerViewAdapter(ArrayList<ImageItemModel> imageItemModel) {
        this.imageItemList = imageItemModel;

    }

    public ImageRecyclerViewAdapter(Context context) {
        this.mcontext = context;
    }


    public ImageRecyclerViewAdapter(Context context, OnItemClickListener onItemClickListener){
        this.mcontext = context;
        this.mListner = onItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_list_item);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        mListner.onItemClick(v, getAdapterPosition());


                    }
                }
            });
        }

       /* public void bind(Bitmap BitmageView){
            imageView.setImageBitmap(BitmageView);
        }*/
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.adpater_imageitem, parent, false);
        MyViewHolder mh = new MyViewHolder(view);
        return mh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bitmap bitmap = imageItemList.get(position).getImageBitmap();
        //Bitmap bitmap = imageBitmapList.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }


    @SuppressLint("LogConditional")
    @Override
    public int getItemCount() {
        Log.d(TAG, "ArrayList getCount : " + String.valueOf(imageItemList.size()));
        return imageItemList.size();
    }
}

