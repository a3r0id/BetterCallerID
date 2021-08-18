package com.a3r0.bettercallerid;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class SystemNotification
{
    public SystemNotification(String ticker, String title, String message, Context context)
    {
        Log.d("NOTIII", "SystemNotification: ");
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, Globals.channelID);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.bci_incoming_foreground)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}
