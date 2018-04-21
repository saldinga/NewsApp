package com.example.android.newsapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
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


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static Context mContext;
    private String urlString = "http://content.guardianapis.com/search";
    private NewsAdapter mAdapter;
    private ProgressBar progressBar;
    private TextView infoText;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        progressBar = findViewById(R.id.progressBar);
        infoText = findViewById(R.id.info_text);

        ListView listView = findViewById(R.id.list);
        listView.setEmptyView(infoText);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        contentLoader(1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                News currentEvent = mAdapter.getItem(i);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(currentEvent.getUrl()));
                startActivity(intent);
            }
        });
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
        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void contentLoader(int id) {
        getSupportLoaderManager().initLoader(id, null, this);
    }

    @Override
    public android.support.v4.content.Loader<List<News>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String categoryPreference = sharedPrefs.getString(
                getString(R.string.settings_category_key),
                getString(R.string.settings_category_default));

        String orderByPreference = sharedPrefs.getString(
                getString(R.string.settings_sort_key),
                getString(R.string.settings_sort_default));

        String dateFromPreference = sharedPrefs.getString(
                getString(R.string.settings_date_key), "");

        String searchPreference = sharedPrefs.getString(
                getString(R.string.settings_search_key), "");

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(urlString);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

// Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("show-fields", "trailText");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "50");

        if (!categoryPreference.equals(getString(R.string.settings_category_default_value)) &
                !categoryPreference.equals(getString(R.string.settings_category_default))) {
            uriBuilder.appendQueryParameter("section", categoryPreference);
        }

        if (!dateFromPreference.isEmpty()) {
            uriBuilder.appendQueryParameter("from-date", dateFromPreference);
        }

        if (!searchPreference.isEmpty()) {
            uriBuilder.appendQueryParameter("q", searchPreference);
        }

        uriBuilder.appendQueryParameter("order-by", orderByPreference);
        uriBuilder.appendQueryParameter("api-key", "test");

        Log.i("URL", uriBuilder.toString());

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<News>> loader, List<News> data) {
        progressBar.setVisibility(View.GONE);

        String errorInfo = Utils.errorMessage();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else if (!checkConnection()) {
            infoText.setText(R.string.info_message_no_internet_connection);
        } else if (!errorInfo.isEmpty()) {
            infoText.setText(errorInfo);
        } else {
            infoText.setText(R.string.info_message_no_data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<News>> loader) {
        mAdapter.clear();
    }

    public boolean checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
