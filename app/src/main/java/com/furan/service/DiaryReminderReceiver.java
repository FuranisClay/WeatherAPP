package com.furan.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.furan.R;

public class DiaryReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long diaryId = intent.getLongExtra("diary_id", -1);
        String title = intent.getStringExtra("title");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "diary_reminder_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "日记提醒", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 点击通知跳转的 Intent
        Intent notificationIntent = new Intent(context, com.furan.activity.DiaryEditorActivity.class);
        notificationIntent.putExtra("diary_id", diaryId);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // 防止异常

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) diaryId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification.Builder builder = new Notification.Builder(context, channelId)
                .setContentTitle("日记提醒")
                .setContentText("你有一项提醒事项：" + title)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);  // 设置点击通知跳转

        if (notificationManager != null) {
            notificationManager.notify((int) diaryId, builder.build());
        }
    }
}

