package com.hht.appmusic.Player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class PlayerManager{
    private static PlayerManager _instance;
    private static MediaPlayer mediaPlayer;
    public static PlayerManager Instance(){
        if(_instance == null){
            _instance = new PlayerManager();
        }
        return _instance;
    }

    public PlayerManager() {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
    }

    public void initMusic(Context context,String uri){
        mediaPlayer = MediaPlayer.create(context, Uri.parse(uri));
    }

    public boolean onCompelete(){
        return mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration();
    }

    public void playMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else {
            mediaPlayer.start();
        }
    }

    public boolean getState(){
        return mediaPlayer.isPlaying();
    }


    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void seekMusic(int pos){
        mediaPlayer.seekTo(pos);
    }

    public void releaseMusic(){
        mediaPlayer.release();
        mediaPlayer = null;
    }


}
