package com.bignerdranch.android.support.controller.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.bignerdranch.android.support.view.IntervalTimePicker;

import org.joda.time.DateTime;

public class TimePickerDialogFragment extends DialogFragment {

    public static final String EXTRA_TIME = "TimePickerDialogFragment.Time";
    public static final String SAVED_HOURS = "TimePickerDialogFragment.Hours";
    public static final String SAVED_MINUTES = "TimePickerDialogFragment.Minutes";
    public static final String SAVED_AM_PM = "TimePickerDialogFragment.AmPm";
    public static final String ARG_TITLE_RES_ID = "TimePickerDialogFragment.TitleResId";
    public static final String ARG_POSITIVE_BUTTON_RES_ID = "TimePickerDialogFragment.PositiveButtonResId";
    public static final String ARG_NEGATIVE_BUTTON_RES_ID = "TimePickerDialogFragment.NegativeButtonResId";
    public static final String ARG_TITLE_STRING = "TimePickerDialogFragment.TitleString";
    public static final String ARG_POSITIVE_BUTTON_TITLE_STRING = "TimePickerDialogFragment.PositiveButtonTitleString";
    public static final String ARG_NEGATIVE_BUTTON_TITLE_STRING = "TimePickerDialogFragment.NegativeButtonTitleString";
    public static final String ARG_TIME = "TimePickerDialogFragment.Time";

    protected int mTitleResId;
    protected int mPositiveResId;
    protected int mNegativeResId;
    protected String mTitleString;
    protected String mPositiveString;
    protected String mNegativeString;
    protected long mTime;

    private IntervalTimePicker mIntervalTimePicker;

    public static class Builder {

        int mTitleResId;
        int mPositiveButtonResId;
        int mNegativeButtonResId;
        String mTitle;
        String mPositiveButtonTitle;
        String mNegativeButtonTitle;
        long mTime;

        public Builder setTitleResId(int titleResId) {
            mTitleResId = titleResId;
            return this;
        }

        public Builder setPositiveButtonResId(int positiveButtonResId) {
            mPositiveButtonResId = positiveButtonResId;
            return this;
        }

        public Builder setNegativeButtonResId(int negativeButtonResId) {
            mNegativeButtonResId = negativeButtonResId;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setPositiveButtonTitle(String positiveButtonTitle) {
            mPositiveButtonTitle = positiveButtonTitle;
            return this;
        }

        public Builder setNegativeButtonTitle(String negativeButtonTitle) {
            mNegativeButtonTitle = negativeButtonTitle;
            return this;
        }

        public Builder setTime(long time) {
            mTime = time;
            return this;
        }

        public TimePickerDialogFragment build() {
            return TimePickerDialogFragment.newInstance(this);
        }
    }

    protected static TimePickerDialogFragment newInstance(Builder builder) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, builder.mTitleResId);
        args.putInt(ARG_POSITIVE_BUTTON_RES_ID, builder.mPositiveButtonResId);
        args.putInt(ARG_NEGATIVE_BUTTON_RES_ID, builder.mNegativeButtonResId);
        args.putString(ARG_TITLE_STRING, builder.mTitle);
        args.putString(ARG_POSITIVE_BUTTON_TITLE_STRING, builder.mPositiveButtonTitle);
        args.putString(ARG_NEGATIVE_BUTTON_TITLE_STRING, builder.mNegativeButtonTitle);
        args.putLong(ARG_TIME, builder.mTime);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        mIntervalTimePicker = new IntervalTimePicker(getActivity());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(mIntervalTimePicker);

        if (mTime > 0) {
            DateTime dateTime = new DateTime(mTime);
            int amPm = dateTime.getHourOfDay() < 12 ? 0 : 1;

            mIntervalTimePicker.setCurrentTime(dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), amPm);
        }

        if (!TextUtils.isEmpty(mTitleString)) {
            alertDialogBuilder.setTitle(mTitleString);
        }
        if (!TextUtils.isEmpty(mPositiveString)) {
            alertDialogBuilder.setPositiveButton(mPositiveString, mPositiveClickListener);
        }
        if (!TextUtils.isEmpty(mNegativeString)) {
            alertDialogBuilder.setNegativeButton(mNegativeString, null);
        }

        if (mTitleResId > 0) {
            alertDialogBuilder.setTitle(mTitleResId);
        }
        if (mPositiveResId > 0) {
            alertDialogBuilder.setPositiveButton(mPositiveResId, mPositiveClickListener);
        }
        if (mNegativeResId > 0) {
            alertDialogBuilder.setNegativeButton(mNegativeResId, null);
        }

        if (savedInstanceState != null) {
            int hours = savedInstanceState.getInt(SAVED_HOURS);
            int minutes = savedInstanceState.getInt(SAVED_MINUTES);
            int am_pm = savedInstanceState.getInt(SAVED_AM_PM);
            mIntervalTimePicker.setCurrentTime(hours, minutes, am_pm);
        }

        return alertDialogBuilder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }

        mTitleResId = args.getInt(ARG_TITLE_RES_ID, 0);
        mPositiveResId = args.getInt(ARG_POSITIVE_BUTTON_RES_ID, 0);
        mNegativeResId = args.getInt(ARG_NEGATIVE_BUTTON_RES_ID, 0);

        mTitleString = args.getString(ARG_TITLE_STRING);
        mPositiveString = args.getString(ARG_POSITIVE_BUTTON_TITLE_STRING);
        mNegativeString = args.getString(ARG_NEGATIVE_BUTTON_TITLE_STRING);
        mTime = args.getLong(ARG_TIME, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_HOURS, mIntervalTimePicker.getCurrentHour());
        outState.putInt(SAVED_MINUTES, mIntervalTimePicker.getCurrentMinute());
        outState.putInt(SAVED_AM_PM, mIntervalTimePicker.getAmPm());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
    }

    private DialogInterface.OnClickListener mPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Fragment fragment = getTargetFragment();

            DateTime dateTime = new DateTime().withTime(mIntervalTimePicker.getCurrentHour(), mIntervalTimePicker.getCurrentMinute(), 0, 0);

            Intent data = new Intent();
            data.putExtra(EXTRA_TIME, dateTime.getMillis());
            int resultCode = which;
            fragment.onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    };
}