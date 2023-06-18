package com.rayrtheii.localfileserver

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.rayrtheii.localfileserver.MainActivity

const val PORT = 8976

class Service : Service() {
    private lateinit var server: Router

    override fun onCreate() { // Only one time
        super.onCreate()
        server = Router(PORT, this)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int
    ): Int { // Every time start is called
        val notificationIntent = Intent(this, MainActivity::class.java)
        var pendingIntent: PendingIntent? = null
        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
            .setContentTitle("Local File Server")
            .setSmallIcon(R.mipmap.app_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        server.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}