package com.daily.image.pdf.imagetopdfconvert.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class ImageFile extends BaseFile implements Parcelable, Serializable {


    public static final Creator<ImageFile> CREATOR = new Creator<ImageFile>() {
        @Override
        public ImageFile[] newArray(int size) {
            return new ImageFile[size];
        }

        @Override
        public ImageFile createFromParcel(Parcel in) {
            ImageFile file = new ImageFile();
            file.setId(in.readLong());
            file.setName(in.readString());
            file.setPath(in.readString());
            file.setSize(in.readLong());
            file.setBucketId(in.readString());
            file.setBucketName(in.readString());
            file.setDate(in.readString());
            file.setSelected(in.readByte() != 0);
            file.setOrientation(in.readInt());
            return file;
        }
    };
    private int orientation;   //0, 90, 180, 270

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeLong(getSize());
        dest.writeString(getBucketId());
        dest.writeString(getBucketName());
        dest.writeString(getDate());
        dest.writeByte((byte) (isSelected() ? 1 : 0));
        dest.writeInt(orientation);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
