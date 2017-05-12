package org.schulcloud.mobile.util.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.files.FileActivity;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    /**
     * Handles the FCM Message here
     * @param remoteMessage push messaged received through firebase
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JsonParser parser = new JsonParser();
        JsonObject message =  parser.parse(remoteMessage.getData().get("news")).getAsJsonObject();
        sendNotification(message.get("title").getAsString(), message.get("body").getAsString());

        // TODO: Implement Callback.
    }

    /**
     * Method to send a notification to the application
     * @param messageTitle is the title as it is set in the push messaged received through firebase
     * @param messageBody is the body as it is set in the push messaged received through firebase
     */
    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, FileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
