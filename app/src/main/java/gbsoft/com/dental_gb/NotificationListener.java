package gbsoft.com.dental_gb;

import android.app.Notification;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

import gbsoft.com.dental_gb.dto.AlarmDTO;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Notification notification = sbn.getNotification();
        Bundle extra = notification.extras;
        String title = extra.getString(Notification.EXTRA_TITLE);
        CharSequence text = extra.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extra.getCharSequence(Notification.EXTRA_SUB_TEXT);
        Icon smallIcon = notification.getSmallIcon();
        Icon largeIcon = notification.getLargeIcon();

        if (sbn.getPackageName().equals(getPackageName()) && sbn.getId() >= 1000) {
            SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            int alarmCnt = sharedPreferences.getInt("cnt", 0);
            editor.putInt("cnt", ++alarmCnt);
            editor.commit();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd hh:mm", Locale.getDefault());
            String date = simpleDateFormat.format(sbn.getPostTime());
            MenuActivity2.showAlarmBadge(alarmCnt);
            CommonClass.sAlarmDTO.add(new AlarmDTO(sbn.getId(), title, text.toString(), date, sbn.getPostTime()));
            Log.d("testttt","onNotificationPosted ~ " + " packageName: " + sbn.getPackageName() +
                    " id: " + sbn.getId() + " postTime: " + sbn.getPostTime() + " title: " + title +
                    " text : " + text + " subText: " + subText);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Notification notification = sbn.getNotification();
        Bundle extra = notification.extras;
        String title = extra.getString(Notification.EXTRA_TITLE);
        CharSequence text = extra.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extra.getCharSequence(Notification.EXTRA_SUB_TEXT);
        Icon smallIcon = notification.getSmallIcon();
        Icon largeIcon = notification.getLargeIcon();

        if (sbn.getPackageName().equals(getPackageName()) && sbn.getId() >= 1000) {
//            SharedPreferences sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//
//            int alarmCnt = sharedPreferences.getInt("cnt", 0);
//            editor.putInt("cnt", --alarmCnt < 0 ? 0 : alarmCnt);
//            editor.commit();
//
//            MenuActivity2.showAlarmBadge(alarmCnt);

//            Log.d("testttt","onNotificationRemoved ~ " + " packageName: " + sbn.getPackageName() +
//                    " id: " + sbn.getId() + " postTime: " + sbn.getPostTime() + " title: " + title +
//                    " text : " + text + " subText: " + subText);
        }
    }
}
