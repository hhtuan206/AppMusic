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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hht.appmusic.Adapter.SongAdapter;
import com.hht.appmusic.Fragment.MusicPlayFragment;
import com.hht.appmusic.Helper.SongHelper;
import com.hht.appmusic.Helper.VersionHelper;
import com.hht.appmusic.Model.Song;
import com.hht.appmusic.Notification.NotificationMusic;
import com.hht.appmusic.Player.PlayerManager;
import com.hht.appmusic.Service.NotificationMusicService;

import java.util.ArrayList;
import java.util.Random;

import static com.hht.appmusic.Constant.ACTION;
import static com.hht.appmusic.Constant.ACTION_NEXT;
import static com.hht.appmusic.Constant.ACTION_PLAY;
import static com.hht.appmusic.Constant.ACTION_PRE;
import static com.hht.appmusic.Constant.CHANEL_ID;
import static com.hht.appmusic.Constant.KEY_BUNDLE;
import static com.hht.appmusic.Constant.NOTIFICATION_NAME;
import static com.hht.appmusic.Constant.PARCELABLE;
import static com.hht.appmusic.Constant.REQUEST_STORAGE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    ListView lvSong;
    ArrayList<Song> songArrayList;
    SongAdapter songAdapter;
    LinearLayout llControl;
    TextView tvMusic, tvArtist;
    ImageButton btnPlay, btnPre, btnNext;
    ImageView ivMusic;
    SwipeRefreshLayout srlListView;
    NotificationManager notificationManager;
    MusicPlayFragment fragment = new MusicPlayFragment();
    Random random;
    int position = 0;

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
        random = new Random();
        try {
            songArrayList = SongHelper.getAllMusic(getApplicationContext());
            showData();
        } catch (Exception e) {
            Toast.makeText(this, "Cần cấp quyền đọc", Toast.LENGTH_SHORT).show();
        }
        llControl.setVisibility(View.GONE);

        if (VersionHelper.isVersionQ()) {
            createNotificationChanel();
            registerReceiver(receiver, new IntentFilter(KEY_BUNDLE));
            startService(new Intent(getBaseContext(), NotificationMusicService.class)
            );
        }

        try {
            setDataByPosition(0);
            llControl.setOnClickListener(this);
            lvSong.setOnItemClickListener(this);

        } catch (Exception e) {
            Toast.makeText(this, "Không có bài hát nào", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(this);

        btnPre.setOnClickListener(this);

        btnPlay.setOnClickListener(this);

        srlListView.setOnRefreshListener(this);

    }

    void permision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }



    private void playMusic() {
        MusicPlayFragment fragment = new MusicPlayFragment();
        fragment.playOrpause();
        if (PlayerManager.Instance().getState()) {
            NotificationMusic.notificationMusic(MainActivity.this, songArrayList.get(position), R.drawable.ic_controls_pause, position, songArrayList.size() - 1);
            btnPlay.setImageResource(R.drawable.ic_controls_pause);
        } else {
            NotificationMusic.notificationMusic(MainActivity.this, songArrayList.get(position), R.drawable.ic_controls_play, position, songArrayList.size() - 1);
            btnPlay.setImageResource(R.drawable.ic_controls_play);
        }
    }


    public void nextMusic() {
        if (position == songArrayList.size() - 1) {
            position = 0;
            putDataIntoFragment();
        } else {
            position++;
            putDataIntoFragment();
        }
    }

    public void prevMusic() {
        if (position == 0) {
            position = songArrayList.size() - 1;
            putDataIntoFragment();
        } else {
            --position;
            putDataIntoFragment();
        }
    }

    public void randomMusic(){
        position = random.nextInt((songArrayList.size() - 0) + 1);
    }

    void putDataIntoFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE, songArrayList.get(position));
        fragment.setArguments(bundle);
        fragment.callOnActivity(getApplicationContext());
        setDataByPosition(position);
        NotificationMusic.notificationMusic(MainActivity.this, songArrayList.get(position), R.drawable.ic_controls_pause, position, songArrayList.size() - 1);
    }

    void setDataByPosition(int pos) {
        ivMusic.setImageBitmap(songArrayList.get(pos).getImg());
        tvMusic.setText("" + songArrayList.get(pos).getTitle());
        tvArtist.setText("" + songArrayList.get(pos).getArtist());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void createNotificationChanel() {
        NotificationChannel channel = new NotificationChannel(CHANEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW);
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showData() {
        songAdapter = new SongAdapter(this, R.layout.custom_row, songArrayList);
        lvSong.setAdapter(songAdapter);
    }


    void init() {
        lvSong = findViewById(R.id.lwSong);
        btnNext = findViewById(R.id.btnNext);
        btnPlay = findViewById(R.id.btnPlay);
        btnPre = findViewById(R.id.btnPre);
        ivMusic = findViewById(R.id.imgMusic);
        tvMusic = findViewById(R.id.twMusic);
        tvMusic.setSelected(true);
        tvArtist = findViewById(R.id.twAuthor);
        llControl = findViewById(R.id.player_control);
        srlListView = findViewById(R.id.swiperefresh);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                nextMusic();
                break;
            case R.id.btnPlay:
                playMusic();
                break;
            case R.id.btnPre:
                prevMusic();
                break;
            case R.id.player_control:
                MusicPlayFragment fragment = new MusicPlayFragment();
                fragment.show(getSupportFragmentManager(), fragment.getTag());
                break;
        }
    }

    @Override
    public void onRefresh() {
        songArrayList.clear();
        songArrayList = SongHelper.getAllMusic(getApplicationContext());
        showData();
        srlListView.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE, songArrayList.get(pos));
        MusicPlayFragment fragment = new MusicPlayFragment();
        fragment.setArguments(bundle);
        fragment.callOnActivity(getApplicationContext());
        setDataByPosition(pos);
        position = pos;
        llControl.setVisibility(View.VISIBLE);
        btnPlay.setImageResource(R.drawable.ic_controls_pause);
        NotificationMusic.notificationMusic(getApplicationContext(), songArrayList.get(position), R.drawable.ic_controls_pause, position, songArrayList.size() - 1);

    }
}