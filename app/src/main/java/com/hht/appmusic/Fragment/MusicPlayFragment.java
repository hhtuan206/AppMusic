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
import android.widget.Toast;

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

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.hht.appmusic.Constant.DATE_FOMAT;
import static com.hht.appmusic.Constant.PARCELABLE;

public class MusicPlayFragment extends BottomSheetDialogFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    static Song song;
    TextView songName, songArtist, currentDuration, totalDuration;
    ImageView imgBackground, songArt;
    ImageButton btnRepeat, btnPrev, btnNext, btnPlayPause, btnShuffle;
    SeekBar songProgress;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FOMAT);
    static boolean reapeat = false, shuffling = false;

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
                try {
                    currentDuration.setText(simpleDateFormat.format(PlayerManager.Instance().getCurrentPosition()));
                    songProgress.setProgress(PlayerManager.Instance().getCurrentPosition());
                    Log.e("", "" + PlayerManager.Instance().getMediaCompletetion());
                    if (PlayerManager.Instance().getMediaCompletetion()) {
                        if (reapeat) {
                            PlayerManager.Instance().repeat();
                        } else {
                            if (shuffling) {
                                ((MainActivity) getActivity()).randomMusic();
                                dismiss();
                                ((MainActivity) getActivity()).nextMusic();
                            } else {
                                dismiss();
                                ((MainActivity) getActivity()).nextMusic();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        if (song != null) {
            initUI();
        }

        btnShuffle.setOnClickListener(this);

        btnRepeat.setOnClickListener(this);

        btnPlayPause.setOnClickListener(this);

        btnNext.setOnClickListener(this);

        btnPrev.setOnClickListener(this);

        songProgress.setOnSeekBarChangeListener(this);

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
                    .error(R.drawable.background_default)
                    .into(imgBackground);
            Glide.with(getContext())
                    .load(song.getImg())
                    .placeholder(R.drawable.background_default)
                    .into(songArt);
        } catch (Exception e) {
            Log.e("fata", e.toString());
        }
        btnPlayPause.setImageResource(R.drawable.ic_controls_pause);
    }

    public void playOrpause() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control_next:
                if(reapeat) return;
                if(shuffling){
                    ((MainActivity) getActivity()).randomMusic();
                }
                ((MainActivity) getActivity()).nextMusic();
                if (song != null) {
                    initUI();
                }
                break;
            case R.id.control_prev:
                if(reapeat) return;
                if(shuffling){
                    ((MainActivity) getActivity()).randomMusic();
                }
                ((MainActivity) getActivity()).prevMusic();
                if (song != null) {
                    initUI();
                }
                break;
            case R.id.control_play_pause:
                playOrpause();
                break;
            case R.id.control_repeat:
                if (reapeat) {
                    reapeat = false;
                    btnRepeat.setImageResource(R.drawable.ic_controls_repeat);
                } else {
                    reapeat = true;
                    btnRepeat.setImageResource(R.drawable.ic_controls_repeat_one);
                }
                break;
            case R.id.control_shuffle:
                if (shuffling) {
                    shuffling = false;
                    Toast.makeText(getContext(), "Random disable", Toast.LENGTH_SHORT).show();
                } else {
                    shuffling = true;
                    Toast.makeText(getContext(), "Random enable", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

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
}
