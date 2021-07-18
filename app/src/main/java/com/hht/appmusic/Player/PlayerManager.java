package com.hht.appmusic.Player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class PlayerManager implements MediaPlayer.OnCompletionListener {
    private static PlayerManager _instance;
    private static MediaPlayer mediaPlayer;
    private boolean state;

    public PlayerManager() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

    }

    public static PlayerManager Instance() {
        if (_instance == null) {
            _instance = new PlayerManager();
        }
        return _instance;
    }

    public void initMusic(Context context, String uri) {
        mediaPlayer = MediaPlayer.create(context, Uri.parse(uri));
        mediaPlayer.setOnCompletionListener(this);
        state = false;
    }

    public void playMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public boolean getState() {
        return mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekMusic(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public void releaseMusic() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void repeat() {
        mediaPlayer.setLooping(true);
    }

    public boolean getMediaCompletetion() {
        return state;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mediaPlayer.isLooping()){
            state = false;
        }else {
            state = true;
        }
    }
}
