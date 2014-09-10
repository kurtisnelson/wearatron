package com.bignerdranch.android.support.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.bignerdranch.android.support.R;

public class IntervalTimePicker extends FrameLayout {

    private static final int AM = 0;
    private static final int PM = 1;

    private static final String[] HOUR_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
    private static final String[] MINUTE_VALUES = new String[] { "00", "30" };
    private static final String[] AM_PM_VALUES = new String[] { "AM", "PM" };

    protected NumberPicker mHourSpinner;
    protected NumberPicker mMinuteSpinner;
    protected NumberPicker mAmPmSpinner;

    public IntervalTimePicker(Context context) {
        this(context, null);
    }

    public IntervalTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_time_picker, this, true);

        mHourSpinner = (NumberPicker) findViewById(R.id.view_time_picker_hour);
        mMinuteSpinner = (NumberPicker) findViewById(R.id.view_time_picker_minute);
        mAmPmSpinner = (NumberPicker) findViewById(R.id.view_time_picker_amPm);

        mHourSpinner.setMinValue(0);
        mHourSpinner.setMaxValue(11);
        mHourSpinner.setDisplayedValues(HOUR_VALUES);
        mHourSpinner.setWrapSelectorWheel(false);
        mHourSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(1);
        mMinuteSpinner.setDisplayedValues(MINUTE_VALUES);
        mMinuteSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mAmPmSpinner.setMinValue(0);
        mAmPmSpinner.setMaxValue(1);
        mAmPmSpinner.setDisplayedValues(AM_PM_VALUES);
        mAmPmSpinner.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    public int getCurrentHour() {
        int hour = mHourSpinner.getValue() + 1;
        if (mAmPmSpinner.getValue() == PM) {
            hour += 12;
        }
        if (hour == 12) {
            return 0;
        } else if (hour == 24) {
            return 12;
        }
        return hour;
    }

    public int getCurrentMinute() {
        return mMinuteSpinner.getValue() * 30;
    }

    public int getAmPm() {
        return mAmPmSpinner.getValue();
    }

    public void setCurrentTime(int hour, int minute, int am_pm) {
        mAmPmSpinner.setValue(am_pm);

        hour = hour == 0 ? 11 : hour - 1;
        hour = hour > 12 ? hour - 12 : hour;
        mHourSpinner.setValue(hour);

        if (0 < minute && minute <= 30) {
            mMinuteSpinner.setValue(1);
        } else if (30 < minute && minute <= 60) {
            mMinuteSpinner.setValue(0);
            if (hour == 10) {
                if (am_pm == AM) {
                    mAmPmSpinner.setValue(PM);
                } else {
                    mAmPmSpinner.setValue(AM);
                }
            }
            hour++;
            hour %= 12;
            mHourSpinner.setValue(hour);
        } else {
            mMinuteSpinner.setValue(0);
        }
    }
}
