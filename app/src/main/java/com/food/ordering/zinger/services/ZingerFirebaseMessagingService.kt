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
import com.food.ordering.zinger.data.model.NotificationModel
import com.food.ordering.zinger.ui.home.HomeActivity
import com.food.ordering.zinger.ui.order.OrderDetailActivity
import com.food.ordering.zinger.ui.order.OrdersActivity
import com.food.ordering.zinger.ui.webview.WebViewActivity
import com.food.ordering.zinger.utils.AppConstants
import com.food.ordering.zinger.utils.EventBus
import com.food.ordering.zinger.utils.StatusHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.*

class ZingerFirebaseMessagingService : FirebaseMessagingService() {

    private val preferencesHelper: PreferencesHelper by inject()

    @ExperimentalCoroutinesApi
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage?.from}")
        Log.d("FCM", "Content: ${remoteMessage?.data}")
        createNotificationChannel()
        remoteMessage.data.let {
            when (it["type"]) {
                AppConstants.NOTIFICATION_TYPE_URL -> {
                    val intent = Intent(this, WebViewActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    val title = it["title"]
                    val message = it["message"]
                    val payload = JSONObject(it["payload"])
                    if (payload.has("url")) {
                        intent.putExtra(AppConstants.URL, payload.getString("url").toString())
                        intent.putExtra(AppConstants.NOTIFICATION_TITLE, title)
                        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, Date().time.toInt(), intent, 0)
                        sendNotificationWithPendingIntent(Date().time.toInt(), title, message, pendingIntent)
                    }
                }
                AppConstants.NOTIFICATION_TYPE_ORDER_STATUS -> {
                    val intent = Intent(this, OrderDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    var title = it["title"]
                    var message = it["message"]
                    val payload = JSONObject(it["payload"])
                    var status = ""
                    var shopName = ""
                    var orderId = ""
                    if (payload.has("shopName")) {
                        shopName = payload.getString("shopName").toString()
                    }
                    if (payload.has("orderId")) {
                        orderId = payload.getString("orderId").toString()
                    }
                    if (payload.has("orderStatus")) {
                        status = payload.getString("orderStatus").toString()
                    }
                    if (title.isNullOrEmpty()) {
                        /*if(payload.has("orderId")){
                            title+=shopName+"Order "+orderId + " - "
                        }*/
                        if (payload.has("orderStatus")) {
                            title += "Order " + StatusHelper.getStatusMessage(status) + " - " + shopName
                        }
                    }
                    if (message.isNullOrEmpty()) {
                        message += StatusHelper.getStatusDetailedMessage(status)
                        when (status) {
                            AppConstants.ORDER_STATUS_READY, AppConstants.ORDER_STATUS_OUT_FOR_DELIVERY -> {
                                if(payload.has("secretKey")){
                                    message+="\nSecret Key: "+payload.getString("secretKey").toString()
                                }
                            }
                        }
                    }
                    intent.putExtra(AppConstants.ORDER_ID, orderId)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, orderId.toInt(), intent, 0)
                    sendNotificationWithPendingIntent(orderId.toInt(), title, message, pendingIntent)
                    //Alerting the order detail activity
                    EventBus.send(NotificationModel(it["type"],it["title"],it["message"],JSONObject(it["payload"])))
                }
                AppConstants.NOTIFICATION_TYPE_NEW_ARRIVAL -> {
                    //TODO navigate to specific shop
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    var title = it["title"]
                    var message = it["message"]
                    val payload = JSONObject(it["payload"])
                    var shopName = ""
                    var shopId = ""
                    if (payload.has("shopName")) {
                        shopName = payload.getString("shopName").toString()
                    }
                    if (payload.has("shopId")) {
                        shopId = payload.getString("shopId").toString()
                    }
                    if (title.isNullOrEmpty()) {
                        title += "New Outlet in you place!"
                    }
                    if (message.isNullOrEmpty()) {
                        message += shopName + " has arrived in your place. Try it out!"
                    }
                    intent.putExtra(AppConstants.SHOP_ID, shopId)
                    val pendingIntent: PendingIntent = PendingIntent.getActivity(this, shopId.toInt(), intent, 0)
                    sendNotificationWithPendingIntent(shopId.toInt(), title, message, pendingIntent)
                }
            }
        }.run {
            remoteMessage.notification?.let {
                sendNotification(Date().time.toInt(), it.title, it.body)
            }
        }
    }

    private fun sendNotification(id: Int, title: String?, message: String?) {
        val builder = NotificationCompat.Builder(applicationContext, "7698")
                .setSmallIcon(R.drawable.ic_zinger_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
        }
    }


    private fun sendNotificationWithPendingIntent(id: Int, title: String?, message: String?, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(this, "7698")
                .setSmallIcon(R.drawable.ic_zinger_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(id, builder.build())
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
        Log.d("FCM", "FCM Token: " + token)
        //preferencesHelper.fcmToken = token
        //No need to update the server about fcm token. because it's already happening in home activity
    }
}