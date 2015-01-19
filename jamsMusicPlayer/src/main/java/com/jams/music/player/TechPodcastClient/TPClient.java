package com.jams.music.player.TechPodcastClient;

/**
 * Created by Ian Feng on 1/18/2015.
 */
import android.util.Log;
import org.apache.http.Header;

import com.jams.music.player.AsyncTasks.AsyncBuildLibraryTask;
import com.loopj.android.http.*;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.List;

public class TPClient {
    private static final String BASE_URL = "http://www.idesignforce.com/techpodcast/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static List<MediaInfo> getMedias(final AsyncBuildLibraryTask task) throws JSONException {
        List<MediaInfo> medias = null;
        TPClient.get("test.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("TPClient", "Only ONE JSONObject");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray myMedias) {
                // Pull out the first event on the public timeline
                Log.d("TPClient", "Total: " + myMedias.length());
                Log.d("TPClient", "Will invoke saving to DB");
                task.saveTechPodcastMedias(myMedias);
            }
        });
        return medias;
    }
}
