package com.flaker.flaker;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;


/**
 * Created by xenovia on 3/12/18.
 */

public class NotificationManagerBruce {

    private Context context;
    private NotificationManager mNotificationManager;

    public NotificationManagerBruce(Context context) {
        this.context = context;
    };

    public void createNotification(String textTitle, String textContent) {
        final String CHANNEL_ID = "SOME_ID";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "my channel";
            String description = " my desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(channel);
        } else {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }



        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(001, mBuilder.build());

    }
}
