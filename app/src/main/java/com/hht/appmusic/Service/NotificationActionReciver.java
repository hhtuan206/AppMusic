package com.hht.appmusic.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.hht.appmusic.Constant.ACTION;
import static com.hht.appmusic.Constant.KEY_BUNDLE;

public class NotificationActionReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(KEY_BUNDLE).putExtra(ACTION,intent.getAction()));
    }


}
