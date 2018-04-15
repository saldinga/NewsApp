package com.example.android.newsapp;

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

public class Utils {

    // Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    private static String mErrorMessage = "";
    private static String websiteErrorInfo = MainActivity.getContext().getResources().getString(R.string.website_error);

    //Empty constructor
    private Utils() {
    }

    private static ArrayList<News> formatJson(String jsonString) {

        ArrayList<News> newsArray = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject response = root.optJSONObject("response");
            JSONArray results = response.optJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String sectionName = item.getString("sectionName");
                String publicationDate = item.getString("webPublicationDate");
                String title = item.getString("webTitle");
                String url = item.getString("webUrl");

                JSONObject fields = item.getJSONObject("fields");
                String trailText = fields.getString("trailText");

                JSONArray tags = item.getJSONArray("tags");
                String authors = "";
                String sep = ", ";
                if (tags.length() > 0) {
                    StringBuilder authorsBuilder = new StringBuilder();
                    for (int j = 0; j < tags.length(); j++) {
                        JSONObject contributor = tags.getJSONObject(j);
                        String contributorName = contributor.getString("webTitle");
                        if (j == (tags.length() - 1)) {
                            sep = "";
                        }
                        authorsBuilder.append(contributorName + sep);
                    }
                    authors = authorsBuilder.toString();
                }
                newsArray.add(new News(sectionName, publicationDate, title, url, trailText, authors));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while converting string to jsonObject");
        }
        return newsArray;
    }

    public static ArrayList<News> extractData(String urlString) {

        //Create URL object
        URL url = createUrl(urlString);
        String jsonString = null;

        try {
            jsonString = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "In extractData makeHttpRequest threw IOException");
        }
        return formatJson(jsonString);
    }

    //Create URL object from url string
    private static URL createUrl(String urlString) {

        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e("Tag", "Malformed URL Exception");
        }
        return url;
    }

    //Make http request to extract json string
    private static String makeHttpRequest(URL url) throws IOException {

        if (url == null) return null;

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                mErrorMessage = websiteErrorInfo + " " + urlConnection.getResponseCode();

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with makeHttpRequest");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Reading from stream
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

    public static String errorMessage() {
        return mErrorMessage;
    }
}
