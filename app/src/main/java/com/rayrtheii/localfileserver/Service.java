package com.rayrtheii.localfileserver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

import static com.rayrtheii.localfileserver.MainActivity.CHANNEL_ID;

public class Service extends android.app.Service {
    public static final int PORT = 8976;

    private Router server;

    @Override
    public void onCreate() { // Only one time
        super.onCreate();

        try {
            server = new Router(PORT, this);
        } catch (IOException e) {
            Log.w("Service", "The Server was unable to start");
            e.printStackTrace();
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // Every time start is called
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Local File Server")
                .setSmallIcon(R.mipmap.app_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        server.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
