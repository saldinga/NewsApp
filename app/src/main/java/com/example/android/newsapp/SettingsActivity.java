package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings_main);

            Preference categoryPreference = findPreference(getString(R.string.settings_category_key));
            bindPreferenceSummaryToValue(categoryPreference);

            Preference dateFrom = findPreference(getString(R.string.settings_date_key));
            bindPreferenceSummaryToValue(dateFrom);

            Preference orderBy = findPreference(getString(R.string.settings_sort_key));
            bindPreferenceSummaryToValue(orderBy);


            Preference search = findPreference(getString(R.string.settings_search_key));
            bindPreferenceSummaryToValue(search);
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            if ((preference instanceof DatePickerPreference) && preferenceString.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.date_pattern), Locale.US);
                preferenceString = timeFormat.format(calendar.getTime());
            }
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            // The code in this method takes care of updating the displayed preference summary after it has been changed
            String stringValue = o.toString();
            Log.i("preference", stringValue);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}

