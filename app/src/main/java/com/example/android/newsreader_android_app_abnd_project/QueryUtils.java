package com.example.android.newsreader_android_app_abnd_project;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving article data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int readTimeoutMS = 10000;
    private static final int connectMS = 15000;
    private static final String RESPONSE = "response";
    private static final String RESULTS= "results";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    /**
     * Query the USGS dataset and return an {@link Article} object to represent a single article.
     */
    public static ArrayList<Article> fetchArticleData(String requestUrl) {
        Log.i(LOG_TAG, "fetchArticleData just called.");
        // Create URL object
        URL articleURL = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(articleURL);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Article} object
        ArrayList<Article> article = extractFeatureFromJson(jsonResponse);

        // Return the {@link Article}
        return article;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL articleURL = null;
        try {
            articleURL = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return articleURL;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL articleURL) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (articleURL == null) {
            return jsonResponse;
        }

        HttpURLConnection articleURLConnection = null;
        InputStream inputStream = null;
        try {
            articleURLConnection = (HttpURLConnection) articleURL.openConnection();
            articleURLConnection.setReadTimeout(readTimeoutMS /* milliseconds */);
            articleURLConnection.setConnectTimeout(connectMS /* milliseconds */);
            articleURLConnection.setRequestMethod("GET");
            articleURLConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (articleURLConnection.getResponseCode() == articleURLConnection.HTTP_OK) {
                inputStream = articleURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + articleURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (articleURLConnection != null) {
                articleURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Article} object by parsing out information
     * about the first article from the input articleJSON string.
     */
    static ArrayList<Article> extractFeatureFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Variables declaration
        String webTitle;
        String articleSection;
        String articleURL;
        String publishedDataTime;
        String imageUrl = null;
        Bitmap myBM;

        // Create an empty ArrayList that we can start adding articles to
        ArrayList<Article> articles = new ArrayList<>();

        // try to parse API results within the object and array
        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responseJsonResponse = baseJsonResponse.getJSONObject(RESPONSE);
            JSONArray resultsArray = responseJsonResponse.getJSONArray(RESULTS);

            // If there are results in the results array
            if (resultsArray.length() > 0) {
                // Extract out the first feature (which is an article)
                // Loop through each result in the array
                for (int i = 0; i < resultsArray.length(); i++) {
                    //Get article JSONObject at position i
                    JSONObject myJsonObject = resultsArray.getJSONObject(i);
                    // Extract “webTitle” for location
                    webTitle = myJsonObject.getString("webTitle");
                    // Extract “publishedDataTime” for publishedDataTime
                    publishedDataTime = myJsonObject.getString("webPublicationDate");
                    // Extract URL
                    articleURL = myJsonObject.getString("webUrl");
                    // Extract section
                    articleSection = myJsonObject.getString("sectionId");
                    //Additional information that may not be included:
                    JSONObject fields = myJsonObject.optJSONObject("fields");

                    // Pulls image thumbnail from result then calls Bitmap converter
                    try {
                        imageUrl = fields.optString("thumbnail");  ///PROBLEM
                        Log.i(LOG_TAG, "thumbnail: " + imageUrl);
                         myBM = ((BitmapDrawable) LoadImageFromWebOperations(imageUrl)).getBitmap();
                    } catch (Exception e) {
                         myBM = null;
                        Log.e(LOG_TAG, "thumbnail missing or pull failed", e);
                    }

                    JSONArray tags = myJsonObject.optJSONArray("tags");
                    String authorName = null;
                    String authorSurname = null;
                    // Author is not always available. Checks if tags are present.
                    if (tags.length() > 0) {
                        JSONObject author = tags.optJSONObject(0);
                        authorName = author.optString("firstName");
                        authorSurname = author.optString("lastName");
                    }

                    // Create Article java object from magnitude, location, and publishedDataTime
                    Article article = new Article(webTitle, publishedDataTime, articleURL, myBM, authorName, authorSurname, articleSection);

                    // Add article to list of articles
                    articles.add(article);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the article JSON results " + articleJSON, e);
        }
        return articles;
    }

    /**
     * Convert the thumbnail URL to bitmap image
     */
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "");
            return d;
        } catch (Exception e) {
            Log.e(LOG_TAG, "LoadImagefromweboperations failed", e);
            return null;
        }
    }

    private QueryUtils() {
    }
}