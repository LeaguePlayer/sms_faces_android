package com.amobile.mems.helpers;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 10.06.13
 * Time: 7:58
 * To change this template use File | Settings | File Templates.
 */
public class MyStruct implements Parcelable {
    public int width, height;
    float x, y, textSize, rotation;
    String path, name, text;
    boolean isImage = true;

    public MyStruct(int width, int height, float x, float y, float rotation, String path, String name) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.path = path;
        this.name = name;
        isImage = true;
    }

    public MyStruct(int width, int height, float x, float y, float rotation, float TextSize, String text) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.textSize = TextSize;
        this.text = text;
        isImage = false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRotation(){
        return rotation;
    }

    public float getTextSize() {
        return textSize;
    }

    public String getText() {
        return text;
    }

    public boolean IsImage() {
        return isImage;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(width);
        parcel.writeInt(height);
        parcel.writeFloat(x);
        parcel.writeFloat(y);
        parcel.writeBooleanArray(new boolean[]{isImage});

        if (isImage) {
            parcel.writeString(path);
            parcel.writeString(name);
        } else {
            parcel.writeFloat(textSize);
            parcel.writeString(text);
        }
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<MyStruct> CREATOR = new Creator<MyStruct>() {
        public MyStruct createFromParcel(Parcel in) {
            return new MyStruct(in);
        }

        public MyStruct[] newArray(int size) {
            return new MyStruct[size];
        }
    };

    private MyStruct(Parcel parcel) {
        width = parcel.readInt();
        height = parcel.readInt();
        x = parcel.readFloat();
        y = parcel.readFloat();

        boolean[] array = new boolean[1];
        parcel.readBooleanArray(array);
        isImage = array[0];


        if (isImage) {
            textSize = 0;
            text = "";
            path = parcel.readString();
            name = parcel.readString();
        } else {
            path = "";
            name = "";
            textSize = parcel.readFloat();
            text = parcel.readString();
        }
    }
}