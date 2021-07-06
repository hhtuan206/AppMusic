package com.hht.appmusic;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hht.appmusic.Adapter.SongAdapter;
import com.hht.appmusic.Fragment.MusicPlayFragment;
import com.hht.appmusic.Helper.SongHelper;
import com.hht.appmusic.Model.Song;
import com.hht.appmusic.Notification.NotificationMusic;
import com.hht.appmusic.Player.PlayerManager;
import com.hht.appmusic.Service.OnClearFromRecentService;

import java.util.ArrayList;

import static com.hht.appmusic.Constant.ACTION;
import static com.hht.appmusic.Constant.ACTION_NEXT;
import static com.hht.appmusic.Constant.ACTION_PLAY;
import static com.hht.appmusic.Constant.ACTION_PRE;
import static com.hht.appmusic.Constant.CHANEL_ID;
import static com.hht.appmusic.Constant.KEY_BUNDLE;
import static com.hht.appmusic.Constant.NOTIFICATION_NAME;
import static com.hht.appmusic.Constant.PARCELABLE;
import static com.hht.appmusic.Constant.REQUEST_STORAGE;

public class MainActivity extends AppCompatActivity {
    ListView lwSong;
    ArrayList<Song> songs;
    SongAdapter songAdapter;
    LinearLayout linearLayout;
    TextView twMusic, twArtist;
    ImageButton btnPlay, btnPre, btnNext;
    ImageView imgMusic;
    SwipeRefreshLayout swipeRefreshLayout;
    NotificationManager notificationManager;
    int position;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString(ACTION);
            switch (action) {
                case ACTION_NEXT:
                    nextMusic();
                    break;
                case ACTION_PLAY:
                    playMusic();
                    break;
                case ACTION_PRE:
                    prevMusic();
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permision();
        setContentView(R.layout.activity_main);
        init();

        try {
            songs = SongHelper.getAllMusic(getApplicationContext());
            showData();
        }catch (Exception e){
            Toast.makeText(this, "Cần cấp quyền đọc", Toast.LENGTH_SHORT).show();
        }
        linearLayout.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChanel();
            registerReceiver(receiver, new IntentFilter(KEY_BUNDLE));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class)
            );

        }

        try {
            setDataByPos(0);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayFragment fragment = new MusicPlayFragment();
                    fragment.show(getSupportFragmentManager(), fragment.getTag());

                }
            });

            lwSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PARCELABLE, songs.get(pos));
                    MusicPlayFragment fragment = new MusicPlayFragment();
                    fragment.setArguments(bundle);
                    ((MusicPlayFragment) fragment).callOnActivity(getApplicationContext());
                    setDataByPos(pos);
                    position = pos;
                    linearLayout.setVisibility(View.VISIBLE);
                    btnPlay.setImageResource(R.drawable.ic_controls_pause);
                    NotificationMusic.notificationMusic(getApplicationContext(), songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Không có bài hát nào", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMusic();
            }
        });

        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMusic();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                songs.clear();
                songs = SongHelper.getAllMusic(getApplicationContext());
                showData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    void permision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    private void playMusic() {
        MusicPlayFragment fragment = new MusicPlayFragment();
        ((MusicPlayFragment) fragment).playOrpause();
        if (PlayerManager.Instance().getState()) {
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
            btnPlay.setImageResource(R.drawable.ic_controls_pause);
        } else {
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_play, position, songs.size() - 1);
            btnPlay.setImageResource(R.drawable.ic_controls_play);
        }
    }

    public void nextMusic() {
        if (position == songs.size() - 1) {
            position = 0;
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
            Bundle bundle = new Bundle();
            bundle.putParcelable(PARCELABLE, songs.get(position));
            MusicPlayFragment fragment = new MusicPlayFragment();
            fragment.setArguments(bundle);
            ((MusicPlayFragment) fragment).callOnActivity(getApplicationContext());
            setDataByPos(position);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PARCELABLE, songs.get(position + 1));
            MusicPlayFragment fragment = new MusicPlayFragment();
            fragment.setArguments(bundle);
            ((MusicPlayFragment) fragment).callOnActivity(getApplicationContext());
            setDataByPos(position + 1);
            position++;
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
        }
    }

    public void prevMusic() {
        if (position == 0) {
            position = songs.size() - 1;
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
            Bundle bundle = new Bundle();
            bundle.putParcelable(PARCELABLE, songs.get(position));
            MusicPlayFragment fragment = new MusicPlayFragment();
            fragment.setArguments(bundle);
            ((MusicPlayFragment) fragment).callOnActivity(getApplicationContext());
            setDataByPos(position);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PARCELABLE, songs.get(position));
            MusicPlayFragment fragment = new MusicPlayFragment();
            fragment.setArguments(bundle);
            ((MusicPlayFragment) fragment).callOnActivity(getApplicationContext());
            setDataByPos(position);
            position--;
            NotificationMusic.notificationMusic(MainActivity.this, songs.get(position), R.drawable.ic_controls_pause, position, songs.size() - 1);
        }
    }

    void setDataByPos(int pos) {
        imgMusic.setImageBitmap(songs.get(pos).getImg());
        twMusic.setText("" + songs.get(pos).getTitle());
        twArtist.setText("" + songs.get(pos).getArtist());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void createChanel() {
        NotificationChannel channel = new NotificationChannel(CHANEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_NONE);
        channel.setVibrationPattern(new long[]{0});
        channel.enableVibration(false);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void showData() {
        songAdapter = new SongAdapter(this, R.layout.custom_row, songs);
        lwSong.setAdapter(songAdapter);
    }


    void init() {
        lwSong = findViewById(R.id.lwSong);
        btnNext = findViewById(R.id.btnNext);
        btnPlay = findViewById(R.id.btnPlay);
        btnPre = findViewById(R.id.btnPre);
        imgMusic = findViewById(R.id.imgMusic);
        twMusic = findViewById(R.id.twMusic);
        twMusic.setSelected(true);
        twArtist = findViewById(R.id.twAuthor);
        linearLayout = findViewById(R.id.playercontrol);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
    }


}