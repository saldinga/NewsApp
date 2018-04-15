package com.example.android.newsapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        News currentEvent = getItem(position);

        // Displaying publication time
        TextView dateTextView = listItemView.findViewById(R.id.time);

        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

        String dateString = currentEvent.getPublicationDate();
        dateString = dateString.replaceAll("T", " ");
        dateString = dateString.replaceAll("Z", "");
        Date parsed = null;
        try {
            parsed = sourceFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error with date parse. Item:" + position);
        }

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        destFormat.setTimeZone(tz);
        String result = destFormat.format(parsed);
        dateTextView.setText(result);

        //Displaying Section name
        TextView sectionName = listItemView.findViewById(R.id.section);
        sectionName.setText(currentEvent.getSectionName());

        //Displaying title
        TextView title = listItemView.findViewById(R.id.title);
        title.setText(currentEvent.getTitle());

        //Displaying author(s) if such exist(s)
        TextView authors = listItemView.findViewById(R.id.author);
        String authorsName = currentEvent.getAuthors();
        if (!authorsName.isEmpty()) {
            authors.setText(authorsName);
        }

        //Displaying trai text
        TextView trailText = listItemView.findViewById(R.id.trail_text);
        String htmltext = Html.fromHtml(currentEvent.getTrailText()).toString();
        trailText.setText(Html.fromHtml(htmltext));

        return listItemView;
    }
}
