package com.dodi.disasteralert;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;

import java.io.IOException;


public class YoutubeActivity extends AppCompatActivity {

    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        new GetSearchDataAsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(SearchListResponse searchListResponse) {
                super.onPostExecute(searchListResponse);
                if (searchListResponse == null) {
                    Log.d("SearchListResponse", "null");
                } else {
                    Log.d("SearchListResponse", searchListResponse.toString());
                }
            }
        }.execute("Atta");
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSearchDataAsyncTask extends AsyncTask<String, Void, SearchListResponse> {
        private static final String YOUTUBE_PART = "snippet";
        private static final String YOUTUBE_FIELDS = "items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url)";

        YouTube mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();

        @Override
        protected SearchListResponse doInBackground(String... params) {

            final String keyword = params[0];

            SearchListResponse searchListResponse;
            try {
                searchListResponse = mYoutubeDataApi.search().list(YOUTUBE_PART)
                        .setQ(keyword)
                        .setMaxResults(Long.parseLong("25"))
                        .setKey(getString(R.string.YOUTUBE_KEY))
                        .setType("video")
                        .setFields(YOUTUBE_FIELDS)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return searchListResponse;
        }
    }
}
