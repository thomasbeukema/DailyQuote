package me.thomasbeukema.dailyquote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if intent is BOOT_COMPLETED
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent i = new Intent(context, NotificationPublisher.class);    // New instance of intent to NotificationPublisher
            i.putExtra(NotificationPublisher.NOTIF_ID, 1);  // Put notification id as extra with intent
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);    // Generate new PendingIntent from intent

            Calendar c = Calendar.getInstance();

            // Check if the time is later than 8am
            if (c.get(Calendar.HOUR_OF_DAY) > 7) {
                // It is later than 8:00 AM
                c.add(Calendar.DAY_OF_MONTH, 1);    // Start next day with notification
            }
            // Set time to 8:00 AM
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            long futureInMillis = c.getTimeInMillis();  // Get millis of calendar
            long currentMillis = System.currentTimeMillis();    // Get current tim in millis
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);   // Get AlarmManager

            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, futureInMillis - currentMillis, 1000 * 60 * 60 * 24, pi);
        }
    }
}
