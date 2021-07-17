package com.hht.appmusic.Helper;

import android.os.Build;

public class VersionHelper {
    public static boolean isVersionQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
