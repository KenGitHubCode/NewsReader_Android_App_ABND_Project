package com.example.android.newsreader_android_app_abnd_project;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    /**
     * Class Variables declaration
     */
    //QA LOG_CAT for debugging
    public static final String LOG_TAG = MainActivity.class.getName();
    //URL for API data from the website
    private static final String GUARDIANAPIS_REQUEST_URL =
            "https://content.guardianapis.com/search?";
    // Adapter for the list of articles
    private ArticleAdapter mAdapter;
    // Constant value for the article loader ID. We can choose any integer.
    // This really only comes into play if you're using multiple loaders.
    private static final int ARTICLE_LOADER_ID = 1;
    //Empty Text View for no data initialize
    private TextView emptyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //identify the empty textview
        emptyText = (TextView) findViewById(R.id.empty_text_view);
        // Find a reference to the {@link ListView} in the layout
        final ListView articleListView = (ListView) findViewById(R.id.list);
        articleListView.setEmptyView(emptyText);
        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Check Internet connectivity via Connectivity Manager
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            //Initiate the Loader
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            //set the empty view text
            emptyText.setText(R.string.no_internet_text);
            // Progress view
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
        }

        Log.i(LOG_TAG, "InitLoader just called.");

        /**
         *  Set on item click listener block
         *  Creates Variable of clicked item, assigns intent values and starts activity
         */
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article selectedArticle = mAdapter.getItem(i);
                String articleURL = selectedArticle.getMyArticleURL();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleURL));

                // Create a new intent to open the {@link applicable activity}
                Intent myIntent = new Intent(browserIntent);

                //In case the devices does not have a browser, check...
                if (myIntent.resolveActivity(getPackageManager()) != null) {
                    // Start the new activity
                    startActivity(myIntent);
                }
            }
        }); // END setOnItemClickListener
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader just called.");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String myCatagory = sharedPrefs.getString(
                getString(R.string.settings_catagory_key),
                getString(R.string.settings_catagory_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIANAPIS_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", "ai");

        // Check if catagory is "all", section is not needed if "all" -meaning no section filter
        if (!myCatagory.equals("all")) {
            uriBuilder.appendQueryParameter("section", myCatagory);
        }

        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-references", "all");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
//        uriBuilder.appendQueryParameter("page-size", numberOfNews);

        uriBuilder.appendQueryParameter("api-key", "77fb4dae-21c2-4f43-9aa6-79b193776bb0");
        Log.i("ArticleActivity", uriBuilder.toString());
        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> loadedArticles) {
        Log.i(LOG_TAG, "onLoadFinished just called.");

        // Clear the adapter of previous article data
        mAdapter.clear();

        if (loadedArticles != null && !loadedArticles.isEmpty()) {
            mAdapter.addAll(loadedArticles);
        }

        //set the empty view text
        emptyText.setText(R.string.no_data_text);

        // Progress view
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.i(LOG_TAG, "onLoaderReset just called.");
        // Clear the adapter of previous article data
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
