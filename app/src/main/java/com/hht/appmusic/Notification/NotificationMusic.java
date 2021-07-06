package com.hht.appmusic.Notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hht.appmusic.Model.Song;
import com.hht.appmusic.R;
import com.hht.appmusic.Service.NotificationActionService;

import static com.hht.appmusic.Constant.*;


public class NotificationMusic {

    public static Notification notification;

    public static void notificationMusic(Context context, Song song, int play, int pos, int size) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, TAG_BROADCAST);
            PendingIntent pendingIntentPre;
            int drv_pre;

            Intent intentPre = new Intent(context, NotificationActionService.class).setAction(ACTION_PRE);
            pendingIntentPre = PendingIntent.getBroadcast(context, 0, intentPre, PendingIntent.FLAG_UPDATE_CURRENT);
            drv_pre = R.drawable.ic_controls_prev;


            Intent intentPlay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent pendingIntentNext;
            int drv_next;

            Intent intentNext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            drv_next = R.drawable.ic_controls_next;


            notification = new NotificationCompat.Builder(context, CHANEL_ID)
                    .setSmallIcon(R.drawable.cd)
                    .setContentTitle(song.getTitle())
                    .setContentText(song.getArtist())
                    .setLargeIcon(song.getImg())
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drv_pre, ACTION_PRE, pendingIntentPre)
                    .addAction(play, ACTION_PLAY, pendingIntentPlay)
                    .addAction(drv_next, ACTION_NEXT, pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
                    .build();

            notificationManagerCompat.notify(1, notification);
        }

    }
}
