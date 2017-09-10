package me.thomasbeukema.dailyquote;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor spe;

    private String TAG = "DailyQuote/Settings";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        spe = sp.edit();

        final SwitchCompat sw = (SwitchCompat) findViewById(R.id.settings_notif_switch);

        if (sp.getBoolean("notification", false)) {
            sw.setChecked(true);
        }

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw.isChecked()) {
                    // Enable notifications

                    Log.i(TAG, "Notif Enabled");
                    spe.putBoolean("notification", true);
                    spe.commit();

                    scheduleNotification();
                } else {
                    // Disable notification
                    spe.putBoolean("notification", false);
                    spe.commit();

                    cancelAlarm();
                }
            }
        });
    }

    private void scheduleNotification() {
        Intent i = new Intent(this, NotificationPublisher.class);
        i.putExtra(NotificationPublisher.NOTIF_ID, 1);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


        Calendar c = Calendar.getInstance();

        if (c.get(Calendar.HOUR_OF_DAY) > 7) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, 8);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long futureInMillis = c.getTimeInMillis();
        long currentMillis = System.currentTimeMillis();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, futureInMillis - currentMillis, 1000 * 60 * 60 * 24, pi);
    }

    private void cancelAlarm() {
        Intent i = new Intent(this, NotificationPublisher.class);
        i.putExtra(NotificationPublisher.NOTIF_ID, 1);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pi);
    }
}
