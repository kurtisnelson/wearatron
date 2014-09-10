package com.bignerdranch.android.support.controller.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.DatePicker;

import org.joda.time.DateTime;

public class DatePickerDialogFragment extends DialogFragment {

    public static final String EXTRA_DATE = "DatePickerDialogFragment.Date";
    public static final String SAVED_DAY = "DatePickerDialogFragment.Day";
    public static final String SAVED_MONTH = "DatePickerDialogFragment.Month";
    public static final String SAVED_YEAR = "DatePickerDialogFragment.Year";
    public static final String ARG_TITLE_RES_ID = "DatePickerDialogFragment.TitleResId";
    public static final String ARG_POSITIVE_BUTTON_RES_ID = "DatePickerDialogFragment.PositiveButtonResId";
    public static final String ARG_NEGATIVE_BUTTON_RES_ID = "DatePickerDialogFragment.NegativeButtonResId";
    public static final String ARG_TITLE_STRING = "DatePickerDialogFragment.TitleString";
    public static final String ARG_POSITIVE_BUTTON_TITLE_STRING = "DatePickerDialogFragment.PositiveButtonTitleString";
    public static final String ARG_NEGATIVE_BUTTON_TITLE_STRING = "DatePickerDialogFragment.NegativeButtonTitleString";
    public static final String ARG_MINIMUM_DATE = "DatePickerDialogFragment.MinimumDate";
    public static final String ARG_DATE = "DatePickerDialogFragment.Date";

    protected int mTitleResId;
    protected int mPositiveResId;
    protected int mNegativeResId;
    protected String mTitleString;
    protected String mPositiveString;
    protected String mNegativeString;
    protected long mMinimumDate;
    protected long mDate;

    private DatePicker mDatePicker;

    public static class Builder {

        int mTitleResId;
        int mPositiveButtonResId;
        int mNegativeButtonResId;
        String mTitle;
        String mPositiveButtonTitle;
        String mNegativeButtonTitle;
        long mMinimumDate;
        long mDate;

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

        public Builder setMinimumDate(long minimumDate) {
            mMinimumDate = minimumDate;
            return this;
        }

        public Builder setDate(long date) {
            mDate = date;
            return this;
        }

        public DatePickerDialogFragment build() {
            return DatePickerDialogFragment.newInstance(this);
        }
    }

    protected static DatePickerDialogFragment newInstance(Builder builder) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, builder.mTitleResId);
        args.putInt(ARG_POSITIVE_BUTTON_RES_ID, builder.mPositiveButtonResId);
        args.putInt(ARG_NEGATIVE_BUTTON_RES_ID, builder.mNegativeButtonResId);
        args.putString(ARG_TITLE_STRING, builder.mTitle);
        args.putString(ARG_POSITIVE_BUTTON_TITLE_STRING, builder.mPositiveButtonTitle);
        args.putString(ARG_NEGATIVE_BUTTON_TITLE_STRING, builder.mNegativeButtonTitle);
        args.putLong(ARG_MINIMUM_DATE, builder.mMinimumDate);
        args.putLong(ARG_DATE, builder.mDate);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        mDatePicker = new DatePicker(getActivity());
        mDatePicker.setCalendarViewShown(false);
        mDatePicker.setSpinnersShown(true);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(mDatePicker);

        if (mMinimumDate > 0) {
            mDatePicker.setMinDate(mMinimumDate);
        }
        if (mDate > 0) {
            DateTime dateTime = new DateTime(mDate);

            mDatePicker.updateDate(dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());
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
            int day = savedInstanceState.getInt(SAVED_DAY);
            int month = savedInstanceState.getInt(SAVED_MONTH);
            int year = savedInstanceState.getInt(SAVED_YEAR);
            mDatePicker.updateDate(year, month, day);
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
        mMinimumDate = args.getLong(ARG_MINIMUM_DATE, 0);
        mDate = args.getLong(ARG_DATE, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_DAY, mDatePicker.getDayOfMonth());
        outState.putInt(SAVED_MONTH, mDatePicker.getMonth());
        outState.putInt(SAVED_YEAR, mDatePicker.getYear());
    }

    private DialogInterface.OnClickListener mPositiveClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Fragment fragment = getTargetFragment();

            DateTime dateTime = new DateTime().withDate(mDatePicker.getYear(), mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());

            Intent data = new Intent();
            data.putExtra(EXTRA_DATE, dateTime.getMillis());
            int resultCode = which;
            fragment.onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    };
}
