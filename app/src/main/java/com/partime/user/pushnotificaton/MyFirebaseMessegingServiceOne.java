package com.partime.user.pushnotificaton;

/**
 * Created by Tech Ugo on 6/09/2019
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.partime.user.R;
import com.partime.user.activities.ChatActivity;
import com.partime.user.activities.HomeActivity;
import com.partime.user.activities.ScheduleTaskActivity;
import com.partime.user.activities.TaskDetailActivity;
import com.partime.user.prefences.AppPrefence;
import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessegingServiceOne extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public AppPrefence appPrefence;
    Intent intent;
    String senderImage, senderName, address, distance, subject, price, rating, stdClass, stgAge, stdId, bookingId,
            duration, bookingTime, currentTime;
    int expiryTime;
    int NOTIFICATION_ID = 1;
    private String title;
    private int recieverId, taskId;
    private String type, msg, body;

    // NotificationResponseModel notificationResponseModel;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        appPrefence = AppPrefence.INSTANCE;
        appPrefence.initAppPreferences(MyFirebaseMessegingServiceOne.this);
      /*  String tempApiToken = appPrefence.getString(AppPrefence.SharedPreferncesKeys.apiToken.toString(), " ");
        if (tempApiToken != null && tempApiToken.trim().length() > 2) {*/
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            int notiFicationCount = 1;
            //  getPushNotificationData();
            JSONObject jObj = new JSONObject(remoteMessage.getData());
            try {
                msg = jObj.optString("body");
                type = jObj.optString("type");
                title = jObj.optString("title");
                body = jObj.optString("body");
                recieverId = jObj.optInt("senderId");
                senderImage = jObj.optString("profilePic");
                senderName = jObj.optString("name");
                taskId = jObj.optInt("taskId");
                if (jObj.has("result")) {
                    String stdDataObj = jObj.getString("result");


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Boolean tempApiToken = appPrefence.getBoolean(AppPrefence.SharedPreferncesKeys.IS_LOGIN.toString());
        if (tempApiToken) {

            if (type.equals("5")) {
                intent = new Intent(this, ChatActivity.class);
                intent.putExtra("RECIEVER_ID", recieverId);
                intent.putExtra("SENDER_PROFILE_PICTURE", senderImage);
                intent.putExtra("SENDER_NAME", senderName);
                intent.putExtra("FLAGS_BACK", 1);

            } else if (type.equals("6") || type.equals("7") || type.equals("8") || type.equals("9")) {

                if (taskId != 0) {
                    intent = new Intent(this, TaskDetailActivity.class);
                    intent.putExtra("TASK_ID", taskId);
                }

            }else if(type.equals("14")) {

                intent = new Intent(this, ScheduleTaskActivity.class);
                intent.putExtra("SCHEDULE_FLAG", 1);
            }else if(type.equals("2")){
                appPrefence.setInt(AppPrefence.SharedPreferncesKeys.IS_ENROLLED.toString(),1);
                intent = new Intent(this, Notification.class);
            }
            else {
                intent = new Intent(this, Notification.class);
            }

        }

        showNotification(msg, title);
        // }
    }

    // [END receive_message]
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "Refreshed token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        //  Implement this method to send token to your app server.
        appPrefence = AppPrefence.INSTANCE;
        appPrefence.initAppPreferences(MyFirebaseMessegingServiceOne.this);
        //SharedPrefrencesManager.getInstance(MyFirebaseMessegingService.this).setString(AppConstant.KEY_DEVICE_TOKEN, token);
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.DEVICE_TOKEN.toString(), token);
    }


    // show notification
    public void showNotification(String title, String alert) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription(getResources().getString(R.string.app_name));
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setVibrate(new long[]{0, 100, 100, 100, 100, 100})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                .setContentIntent(pi)
                .setAutoCancel(true)
                //.setNumber()
                .setContentText(alert);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.notifications_icon);
        } else {
            builder.setSmallIcon(R.mipmap.notifications_icon);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}