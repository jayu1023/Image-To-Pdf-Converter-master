package com.daily.image.pdf.imagetopdfconvert.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseFile implements Parcelable {
    public static final Creator<BaseFile> CREATOR = new Creator<BaseFile>() {
        @Override
        public BaseFile[] newArray(int size) {
            return new BaseFile[size];
        }

        @Override
        public BaseFile createFromParcel(Parcel in) {
            BaseFile file = new BaseFile();
            file.id = in.readLong();
            file.name = in.readString();
            file.path = in.readString();
            file.size = in.readLong();
            file.dateTitle = in.readString();
            file.position = in.readInt();
            file.bucketId = in.readString();
            file.bucketName = in.readString();
            file.date = in.readString();
            file.isSelected = in.readByte() != 0;
            return file;
        }
    };
    private long id;
    private String dateTitle;
    private int position;
    private boolean isDirectory;
    private String name;
    private String path;
    private long size;   //byte
    private String bucketId;  //Directory ID
    private String bucketName;  //Directory Name
    private String date;  //Added Date

    private boolean isSelected;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseFile)) return false;

        BaseFile file = (BaseFile) o;
        return this.path.equals(file.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public String getDateTitle() {
        return dateTitle;
    }

    public void setDateTitle(String dateTitle) {
        this.dateTitle = dateTitle;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(dateTitle);
        dest.writeInt(position);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeLong(size);
        dest.writeString(bucketId);
        dest.writeString(bucketName);
        dest.writeString(date);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
