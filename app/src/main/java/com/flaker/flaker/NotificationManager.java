package com.flaker.flaker;

import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by xenovia on 3/12/18.
 */

public class NotificationManager {

    private Context context;

    public NotificationManager(Context context) {
        this.context = context;
    };

    public void createNotification(String textTitle, String textContent) {
        final String CHANNEL_ID = "SOME_ID";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
