package com.example.android.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class DatePickerPreference extends DialogPreference implements
        OnDateChangedListener {

    // Tag for the log messages
    private static final String LOG_TAG = DatePickerPreference.class.getSimpleName();
    private int mSelectedDay;
    private int mSelectedMonth;
    private int mSelectedYear;
    private Context mContext;
    private SharedPreferences preferences;

    public DatePickerPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

        mContext = ctxt;
        preferences = mContext.getSharedPreferences(mContext.getString(R.string.settings_date_key), mContext.MODE_PRIVATE);
    }

    @Override
    protected View onCreateDialogView() {
        DatePicker picker = new DatePicker(getContext());

        String prefString = preferences.getString(mContext.getString(R.string.settings_date_key), "");

        long mDate = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        if (!prefString.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat(mContext.getString(R.string.date_pattern), Locale.US);
            try {
                calendar.setTime(sdf.parse(prefString));

                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error parsing date");
            }
        }

        picker.init(year, month, day, this);
        picker.setMaxDate(mDate);

        return (picker);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            Calendar cal = Calendar.getInstance();
            cal.set(mSelectedYear, mSelectedMonth, mSelectedDay);

            SimpleDateFormat timeFormat = new SimpleDateFormat(mContext.getString(R.string.date_pattern), Locale.US);
            String date = timeFormat.format(cal.getTime());

            //to invoke onPreferenceChange method
            if (callChangeListener(date)) {

                preferences = getSharedPreferences();
                SharedPreferences.Editor edit = preferences.edit();

                edit.putString(mContext.getString(R.string.settings_date_key), date);

                edit.apply();
            }
        }
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
        mSelectedDay = i2;
        mSelectedMonth = i1;
        mSelectedYear = i;
    }

}