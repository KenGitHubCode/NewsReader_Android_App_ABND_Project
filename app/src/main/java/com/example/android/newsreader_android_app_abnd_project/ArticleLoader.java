package com.example.android.newsreader_android_app_abnd_project;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Ken Muckey on 7/15/2018.
 */
public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    /** Tag for log messages */
    private static final String LOG_TAG = ArticleLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(LOG_TAG, "onStartLoading just called.");

    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Article> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground just called.");

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of articles.
        List<Article> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
}