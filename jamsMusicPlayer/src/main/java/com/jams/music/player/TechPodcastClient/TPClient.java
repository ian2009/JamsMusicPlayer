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
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TPClient {
    private static final String BASE_URL = "http://www.idesignforce.com/techpodcast/";
    private static final String VER_URL = "ver.php";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final TPClient instance = new TPClient();
    private CountDownLatch countDownLatchDir = null;
    private CountDownLatch countDownLatchCategory = null;

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
        return instance;
    }

    public void onDirectoryResponse(boolean isOk) {
        if (this.countDownLatchDir != null) {
            this.countDownLatchDir.countDown();
        }
    }

    public void setCategoryNum(int num) {
        if (num > 0) {
            this.countDownLatchCategory = new CountDownLatch(num);
        }
    }

    public void onCategoryResponse(boolean isOk) {
        if (this.countDownLatchCategory != null) {
            this.countDownLatchCategory.countDown();
        }
    }

    public void getMedias(final AsyncBuildLibraryTask task) throws JSONException {
        this.countDownLatchDir = new CountDownLatch(1);
        TPClient.get(VER_URL, null, new DirectoyHandler(task));
        try {
            this.countDownLatchDir.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.countDownLatchDir = null;
    }

    public void getMedias(final AsyncBuildLibraryTask task, final Category category) {
        TPClient.get(category.getQueryUrl(), null, new CategoryHandler(task, category));
    }

    private class DirectoyHandler extends JsonHttpResponseHandler {
        private final AsyncBuildLibraryTask task;

        public DirectoyHandler(AsyncBuildLibraryTask task) {
            this.task = task;
        }


        public void onSuccessInternal(int statusCode, Header[] headers, JSONObject response) {
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
            }

            if (csMap.size() > 0) {
                setCategoryNum(csMap.size());
            }

            //Get the medias.
            for (Map.Entry<String, Category> entry : csMap.entrySet())
            {
                Category c = entry.getValue();
                getMedias(this.task, c);
            }

            if (countDownLatchCategory != null) {
                try {
                    countDownLatchCategory.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            countDownLatchCategory = null;

            Log.d("TPClient", dir.toString());
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            onSuccessInternal(statusCode, headers, response);
            onDirectoryResponse(true);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray myMedias) {
            onDirectoryResponse(true);
        }

        @Override
        public void onFailure(java.lang.Throwable throwable, org.json.JSONObject jsonObject) {
            super.onFailure(throwable, jsonObject);
            onDirectoryResponse(false);
        }

        @Override
        public void onFailure(java.lang.Throwable throwable, org.json.JSONArray jsonArray) {
            super.onFailure(throwable, jsonArray);
            onDirectoryResponse(false);
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
            onCategoryResponse(false);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray myMedias) {
            // Pull out the first event on the public timeline
            Log.d("TPClient", "Total: " + myMedias.length());
            Log.d("TPClient", "Will invoke saving to DB");
            task.saveTechPodcastMedias(myMedias, this.category);
            onCategoryResponse(true);
        }

        @Override
        public void onFailure(java.lang.Throwable throwable, org.json.JSONObject jsonObject) {
            super.onFailure(throwable, jsonObject);
            onCategoryResponse(false);
        }

        @Override
        public void onFailure(java.lang.Throwable throwable, org.json.JSONArray jsonArray) {
            super.onFailure(throwable, jsonArray);
            onCategoryResponse(false);
        }
    }
}
