package org.schulcloud.mobile.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.dashboard.DashboardFragment
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.notifications.CallbackRequest
import org.schulcloud.mobile.models.notifications.NotificationRepository

object FirebaseUtil: FirebaseMessagingService() {
    private val TAG = "FBM Service"

    class NotificationCallback: RequestJobCallback(){
        override fun onError(code: ErrorCode) {
            Log.i(TAG,"Unable to send callback!")
        }

        override fun onSuccess() {
            Log.i(TAG, "Successfully sent callback.")
        }
    }

    private val KEY_TITLE = "title"
    private val KEY_BODY = "body"
    private val KEY_NEWS = "news"
    private val KEY_NOTIFICATION_ID = "notificationId"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        var parser = JsonParser()
        var message = parser.parse(remoteMessage!!.data.get(KEY_NEWS)) as JsonObject
        sendNotification(message.get(KEY_TITLE).asString, message.get(KEY_BODY).asString)
        val notificationId = parser.parse(remoteMessage.data.get(KEY_NOTIFICATION_ID)).asString
        sendCallback(notificationId)
    }

    private fun sendCallback(notificationId: String){
        var callbackRequest: CallbackRequest =  CallbackRequest(notificationId,CallbackRequest.TYPE_RECIEVED)
        NotificationRepository.sendCallback(callbackRequest,NotificationCallback())
    }

    private fun sendNotification(messageTitle: String, messageBody: String){
        var intent = Intent(this,DashboardFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var notifcationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

        var wearableExtender = NotificationCompat.WearableExtender()
                .setHintHideIcon(true)
                .setContentIcon(R.mipmap.ic_launcher)
        notifcationBuilder.extend(wearableExtender)

        var notificationManager = Context.NOTIFICATION_SERVICE as NotificationManager
        notificationManager.notify(0,notifcationBuilder.build())
    }
}