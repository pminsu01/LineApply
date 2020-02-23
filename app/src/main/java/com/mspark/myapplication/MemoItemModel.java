package com.mspark.myapplication;

public class MemoItemModel {
    private int _id;
    private String memoTitle;
    private String memoContents;
    private String memoImage;
    private String memoDate;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMemoTitle() {
        return memoTitle;
    }

    public void setMemoTitle(String memoTitle) {
        this.memoTitle = memoTitle;
    }

    public String getMemoContents() {
        return memoContents;
    }

    public void setMemoContents(String memoContents) {
        this.memoContents = memoContents;
    }

    public String getMemoImage() {
        return memoImage;
    }

    public void setMemoImage(String memoImage) {
        this.memoImage = memoImage;
    }

    public String getMemoDate() {
        return memoDate;
    }

    public void setMemoDate(String memoDate) {
        this.memoDate = memoDate;
    }
}
