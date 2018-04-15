package com.example.android.newsapp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static Context mContext;
    private String urlString = "http://content.guardianapis.com/search?show-fields=trailText&show-tags=contributor&page-size=150&api-key=test";
    private NewsAdapter mAdapter;
    private ProgressBar progressBar;
    private ListView listView;
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

        listView = findViewById(R.id.list);
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

    private void contentLoader(int id) {
        getSupportLoaderManager().initLoader(id, null, this);
    }

    @Override
    public android.support.v4.content.Loader<List<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, urlString);
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
