package com.hht.appmusic.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    private String title;
    private String path;
    private String artist;
    private Bitmap img;

    public Song() {
    }

    public Song(String title, String path,Bitmap img,String artist) {
        this.title = title;
        this.path = path;
        this.img = img;
        this.artist = artist;
    }

    protected Song(Parcel in) {
        title = in.readString();
        path = in.readString();
        artist = in.readString();
        img = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(path);
        dest.writeString(artist);
        dest.writeParcelable(img, flags);
    }
}
