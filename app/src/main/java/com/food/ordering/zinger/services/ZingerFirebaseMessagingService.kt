package com.food.ordering.zinger.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.local.PreferencesHelper
import com.food.ordering.zinger.ui.order.OrderDetailActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject

class ZingerFirebaseMessagingService : FirebaseMessagingService() {

    private val preferencesHelper: PreferencesHelper by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage?.from}")
        Log.d("FCM", "Content: ${remoteMessage?.data}")
        createNotificationChannel()
        remoteMessage.data.let {
            val intent = Intent(this, OrderDetailActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            val builder = NotificationCompat.Builder(this, "7698")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle("Out for delivery")
                    .setContentText("Your order from sathyas is out for delivery")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            with(NotificationManagerCompat.from(applicationContext)) {
                // TODO change id
                notify(123, builder.build())
            }
        }.run{
            remoteMessage.notification?.let {
                var builder = NotificationCompat.Builder(applicationContext, "7698")
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle(remoteMessage.notification!!.title)
                        .setContentText(remoteMessage.notification!!.body)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                with(NotificationManagerCompat.from(applicationContext)) {
                    // TODO change id
                    notify(123, builder.build())
                }
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Orders"
            val descriptionText = "Alerts about order status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("7698", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        //handle token
        Log.d("FCM","FCM Token: "+token)
        preferencesHelper.fcmToken = token
        //TODO update the server about fcm token
    }
}