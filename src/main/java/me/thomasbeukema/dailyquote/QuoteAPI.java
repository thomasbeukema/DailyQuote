package me.thomasbeukema.dailyquote;

import com.goebl.david.Webb;

import org.json.JSONObject;

public class QuoteAPI {

    private Webb w;

    public QuoteAPI() {
        // Init Webb
        w = Webb.create();
    }

    public JSONObject getQuote() {
        // Get quote from forismatic as described on http://forismatic.com/en/api/
        JSONObject result = w
                .get("http://api.forismatic.com/api/1.0/")
                .param("method", "getQuote")
                .param("format", "json")
                .param("key", 6)
                .param("lang", "en")
                .retry(1, false)
                .asJsonObject()
                .getBody();

        // Return JSON
        return result;
    }
}
