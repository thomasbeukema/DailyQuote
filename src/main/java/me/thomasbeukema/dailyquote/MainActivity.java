package me.thomasbeukema.dailyquote;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String TAG = "DailyQuote/MainActivity";
    private final Context ctx = this;

    private String currentQuote;
    private String currentAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create new instance of QuoteOperation, to get quote async
        QuoteOperation qo = new QuoteOperation();
        // Execute QuoteOperation
        qo.execute();

        // Get fab to set onClickListener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new share intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentQuote + " - " + currentAuthor);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share this quote"));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gets called when you select a option from the menu
        if (item.getItemId() == R.id.refresh) {
            // Load a new quote

            // Create snackbar
            ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.main_layout);
            Snackbar.make(cl, "Refreshing...", Snackbar.LENGTH_SHORT).show();

            // New instance of QuoteOperation
            QuoteOperation qo = new QuoteOperation();
            // Execute QuoteOperation
            qo.execute();

            return false;
        } else if (item.getItemId() == R.id.settings) {
            // Go to settings activity

            // New intent to settingsActivity
            Intent i = new Intent(this, SettingsActivity.class);
            // Start activity
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate main menu
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    private class QuoteOperation extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            // New instance of quoteAPI to get a new quote
            QuoteAPI qa = new QuoteAPI();
            // Store result in JSONObject
            JSONObject jo = qa.getQuote();

            return jo;
        }

        @Override
        protected void onPostExecute(JSONObject jo) {
            // Get quote and author textviews
            TextView quote  = (TextView) findViewById(R.id.quote);
            TextView author = (TextView) findViewById(R.id.author);

            try {
                // Extract quote and author from JSONObject
                String quoteString  = "\"" + jo.getString("quoteText").trim() + "\"";
                String authorString = "(" + jo.getString("quoteAuthor").trim() + ")";

                // Set globals
                currentQuote = quoteString;
                currentAuthor = authorString;

                // Set the contents of the textviews to quote and author
                quote.setText(quoteString);
                author.setText(authorString);
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
