package me.thomasbeukema.dailyquote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationPublisher extends BroadcastReceiver{

    private String TAG = "DailyQuote/NotifPub";
    public static String NOTIF_ID = "quote_notif";

    private Context ctx;
    private Intent intent;

    public void onReceive(Context context, Intent i) {
        ctx = context;                              // Set context to use in QuoteOperation
        intent = i;                                 // Set intent to use in QuoteOperation

        QuoteOperation qo = new QuoteOperation();   // New instance of QuoteOperation to perform async
        qo.execute();                               // Execute QO
    }

    private class QuoteOperation extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            QuoteAPI qa = new QuoteAPI();           // New instance of QuoteAPI
            JSONObject jo = qa.getQuote();          // Get quote in JSON format

            return jo;
        }

        @Override
        protected void onPostExecute(JSONObject jo) {
            try {
                // Get notification manager
                NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

                // Extract and prettify quote and author from JSON
                String quoteString  = "\"" + jo.getString("quoteText").trim() + "\"";
                String authorString = "(" + jo.getString("quoteAuthor").trim() + ")";

                // Append author to quote
                String content = quoteString + " - " + authorString;

                // Create new notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
                builder.setContentTitle(authorString);
                builder.setContentText(quoteString);
                builder.setSmallIcon(R.mipmap.ic_launcher_round);
                builder.setSubText("Your daily quote");

                int id = intent.getIntExtra(NOTIF_ID, 0); // Get notification id

                // Set share action on notification
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, quoteString);
                sendIntent.setType("text/plain");
                PendingIntent pi = PendingIntent.getActivity(ctx, id, Intent.createChooser(sendIntent, "Share this quote"), PendingIntent.FLAG_UPDATE_CURRENT);

                builder.addAction(R.drawable.ic_share_variant_white_24dp, "Share", pi);


                // Create new BigTextStyle bc most quotes don't fit in a normal notification
                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle("Your daily quote");
                bigTextStyle.bigText(content);  // Set contents of this big notification
                builder.setStyle(bigTextStyle);

                // Make notification vibrate
                builder.setPriority(Notification.PRIORITY_DEFAULT);
                builder.setDefaults(Notification.DEFAULT_VIBRATE);

                // Finalise notification and notify
                Notification n = builder.build();
                nm.notify(id, n);
            } catch (JSONException je) {
                // Error in JSON
                Log.e(TAG, je.toString());
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
