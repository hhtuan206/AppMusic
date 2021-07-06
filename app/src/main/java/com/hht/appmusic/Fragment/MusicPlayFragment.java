package com.hht.appmusic.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hht.appmusic.MainActivity;
import com.hht.appmusic.Model.Song;
import com.hht.appmusic.Player.PlayerManager;
import com.hht.appmusic.R;

import java.text.SimpleDateFormat;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.hht.appmusic.Constant.*;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicPlayFragment extends BottomSheetDialogFragment {
    TextView songName, songArtist, currentDuration, totalDuration;
    ImageView imgBackground, songArt;
    ImageButton btnRepeat, btnPrev, btnNext, btnPlayPause, btnShuffle;
    SeekBar songProgress;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FOMAT);
    static Song song;

    public MusicPlayFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void realTime() {
        setTimeTotal();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentDuration.setText(simpleDateFormat.format(PlayerManager.Instance().getCurrentPosition()));
                songProgress.setProgress(PlayerManager.Instance().getCurrentPosition());
                if (PlayerManager.Instance().onCompelete()) {
                    ((MainActivity) getActivity()).nextMusic();
                    dismiss();
                }
                handler.postDelayed(this, 500);

            }
        }, 100);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_play_fragment, container, false);
        initWiget(view);
        if (getArguments() != null) {
            song = getArguments().getParcelable(PARCELABLE);
            PlayerManager.Instance().releaseMusic();
            PlayerManager.Instance().initMusic(getContext(), song.getPath());
            PlayerManager.Instance().playMusic();

        }
        if(song != null){
            initUI();
        }

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrpause();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).nextMusic();
                if(song != null){
                    initUI();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).prevMusic();
                if(song != null){
                    initUI();
                }
            }
        });

        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerManager.Instance().seekMusic(seekBar.getProgress());
            }
        });
        return view;
    }

    private void initUI() {
        try {
            totalDuration.setText(simpleDateFormat.format(PlayerManager.Instance().getDuration()));
            songName.setText(song.getTitle());
            songArtist.setText(song.getArtist());
            realTime();
            Glide.with(getContext())
                    .load(song.getImg())
                    .apply(bitmapTransform(new BlurTransformation(10))) // (change according to your wish)
                    .error(R.drawable.cd)
                    .into(imgBackground);
            Glide.with(getContext())
                    .load(song.getImg())
                    .placeholder(R.drawable.cd)
                    .into(songArt);
        } catch (Exception e) {
            Log.e("fata", e.toString());
        }
        btnPlayPause.setImageResource(R.drawable.ic_controls_pause);
    }

    public void playOrpause(){
        try {
            PlayerManager.Instance().playMusic();
            if (PlayerManager.Instance().getState()) {
                btnPlayPause.setImageResource(R.drawable.ic_controls_pause);

            } else {
                btnPlayPause.setImageResource(R.drawable.ic_controls_play);
            }
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    void setTimeTotal() {
        totalDuration.setText(simpleDateFormat.format(PlayerManager.Instance().getDuration()));
        songProgress.setMax(PlayerManager.Instance().getDuration());
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable(PARCELABLE, song);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    public void callOnActivity(Context context) {
        song = getArguments().getParcelable(PARCELABLE);
        PlayerManager.Instance().releaseMusic();
        PlayerManager.Instance().initMusic(context, song.getPath());
        PlayerManager.Instance().playMusic();
    }


    void initWiget(View view) {
        songName = view.findViewById(R.id.song_name);
        songArtist = view.findViewById(R.id.song_artist);
        songArt = view.findViewById(R.id.song_art);
        currentDuration = view.findViewById(R.id.current_duration);
        totalDuration = view.findViewById(R.id.total_duration);
        imgBackground = view.findViewById(R.id.imgBackground);
        btnRepeat = view.findViewById(R.id.control_repeat);
        btnShuffle = view.findViewById(R.id.control_shuffle);
        songProgress = view.findViewById(R.id.song_progress);
        btnPrev = view.findViewById(R.id.control_prev);
        btnPlayPause = view.findViewById(R.id.control_play_pause);
        btnNext = view.findViewById(R.id.control_next);
    }
}
