package com.example.labtech.newsapp;

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
import java.util.List;

/**
 * Helper methods related to requesting and receiving article data from Guardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_SECTION_NAME = "sectionName";
    private static final String KEY_WEB_TITLE = "webTitle"; //it is used both for the article and the contributor name
    private static final String KEY_PUBLICATION_DATE = "webPublicationDate";
    private static final String KEY_WEB_URL = "webUrl";
    private static final String KEY_TAGS = "tags";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian API dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticlesData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
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
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractFeatureFromJson(String articlesJSON) {


        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articlesJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articlesJSON);
            JSONObject response = baseJsonResponse.getJSONObject(KEY_RESPONSE);

            // Extract the JSONArray associated with the key called reults,
            // which represents a list of results (or articles).
            JSONArray articleArray = response.getJSONArray(KEY_RESULTS);

            // For each article in the articleArray, create an {@link Article} object
            for (int i = 0; i < articleArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject results = articleArray.getJSONObject(i);

                // Extract the value for the key called KEY_WEB_TITLE
                String title = results.getString(KEY_WEB_TITLE);
                // Extract the value for the key called KEY_SECTION_NAME
                String section = results.getString(KEY_SECTION_NAME);
                // Extract the value for the key called KEY_WEB_URL
                String url = results.getString(KEY_WEB_URL);
                // Extract the value for the key called KEY_WEB_URL
                String date = results.getString(KEY_PUBLICATION_DATE);
                //delete the time information and leave only the date
                date = date.substring(0, date.length() - 10);
                String allContributors = "";

                JSONArray tagsArray = results.getJSONArray(KEY_TAGS);
                //check if there is a tags array and if it contains any elements
                if (tagsArray != null && tagsArray.length() > 0) {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        // Get a single contributor at position j within the array of articles
                        JSONObject contributor = tagsArray.getJSONObject(j);
                        // Extract the value for the key called KEY_WEB_TITLE
                        allContributors += contributor.getString(KEY_WEB_TITLE) + ", ";
                    }

                } else {
                    allContributors = "Contributors N/A  ";
                }

                //delete the ", " in the end of all contributors
                allContributors = allContributors.substring(0, allContributors.length() - 2);

                // Create a new {@link Article} object with the magnitude, location, time,
                // and url from the JSON response.
                Article article = new Article(title, allContributors, date, section, url);

                // Add the new {@link Article} to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);

        }


        // Return the list of articles
        return articles;
    }


}