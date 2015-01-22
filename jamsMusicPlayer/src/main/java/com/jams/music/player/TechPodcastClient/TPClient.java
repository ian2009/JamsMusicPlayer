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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TPClient {
    private static final String BASE_URL = "http://www.idesignforce.com/techpodcast/";
    private static final String VER_URL = "ver.php";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static TPClient instance = null;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static synchronized TPClient ins() {
        if (instance == null) {
            instance = new TPClient();
        }
        return instance;
    }


    public void getMedias(final AsyncBuildLibraryTask task) throws JSONException {
        TPClient.get(VER_URL, null, new DirectoyHandler(task));
    }

    public void getMedias(final AsyncBuildLibraryTask task, final Category category) {
        TPClient.get(category.getQueryUrl(), null, new CategoryHandler(task, category));
    }

    private class DirectoyHandler extends JsonHttpResponseHandler {
        private final AsyncBuildLibraryTask task;

        public DirectoyHandler(AsyncBuildLibraryTask task) {
            this.task = task;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // If the response is JSONObject instead of expected JSONArray
            Log.d("TPClient", "Only ONE JSONObject");
            Directory dir = new Directory();
            try {
                dir.setAppVer(response.getInt("app_ver"));
                dir.setLibVer(response.getInt("lib_ver"));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            //parse category
            HashMap<String, Category> csMap = dir.getCategories();
            JSONArray cs = null;
            try {
                cs = response.getJSONArray("categories");
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            for (int i = 0; cs != null && i < cs.length(); ++i) {
                Category c = new Category();
                JSONObject obj = null;
                try {
                    obj = cs.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                try {
                    c.setName(obj.getString("name"));
                    c.setVer(obj.getInt("ver"));
                    c.setQueryUrl(obj.getString("query_url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }

                if (c.getName().length() < 1 || c.getQueryUrl().length() < 1) continue;

                csMap.put(c.getName(), c);

                //Get the medias.
                getMedias(this.task, c);
            }

            Log.d("TPClient", dir.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray myMedias) {

        }
    }

    private class CategoryHandler extends JsonHttpResponseHandler {
        private final AsyncBuildLibraryTask task;
        private final Category category;

        public CategoryHandler(AsyncBuildLibraryTask task, Category category) {
            this.task = task;
            this.category = category;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray myMedias) {
            // Pull out the first event on the public timeline
            Log.d("TPClient", "Total: " + myMedias.length());
            Log.d("TPClient", "Will invoke saving to DB");
            task.saveTechPodcastMedias(myMedias, this.category);
        }
    }

}
