package com.hht.appmusic.Helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import com.hht.appmusic.Model.Song;
import com.hht.appmusic.R;

import java.util.ArrayList;

public class SongHelper {

    public static ArrayList<Song> getAllMusic(Context context) {
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC + "!=0",
                null,
                null);
        if (cursor != null && cursor.moveToNext()) {
            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int path = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Song song = new Song();
                song.setTitle(cursor.getString(title));
                song.setPath(cursor.getString(path));
                song.setArtist(cursor.getString(artist));
                retriever.setDataSource(cursor.getString(path));
                if (retriever.getEmbeddedPicture() != null) {
                    byte[] embedPic = retriever.getEmbeddedPicture();
                    song.setImg(BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length));
                } else {
                    song.setImg(BitmapFactory.decodeResource(context.getResources(), R.drawable.cd));
                }
                songList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }
}
