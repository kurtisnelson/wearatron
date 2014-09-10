package com.bignerdranch.android.support.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.bignerdranch.android.support.AppConstants;

import java.util.ArrayList;

public class AlertDialogFragment extends DialogFragment {
    protected final String TAG = AppConstants.APP_TAG + ((Object) this).getClass().getSimpleName();

    public static final String EXTRA_WHICH = "AlertDialogFragment.Which";
    public static final String ARG_TITLE_RES_ID = "AlertDialogFragment.TitleResId";
    public static final String ARG_MESSAGE_RES_ID = "AlertDialogFragment.MessageResId";
    public static final String ARG_POSITIVE_BUTTON_RES_ID = "AlertDialogFragment.PositiveButtonResId";
    public static final String ARG_NEUTRAL_BUTTON_RES_ID = "AlertDialogFragment.NeutralButtonResId";
    public static final String ARG_NEGATIVE_BUTTON_RES_ID = "AlertDialogFragment.NegativeButtonResId";
    public static final String ARG_TITLE_STRING = "AlertDialogFragment.TitleString";
    public static final String ARG_MESSAGE_STRING = "AlertDialogFragment.MessageString";
    public static final String ARG_POSITIVE_BUTTON_TITLE_STRING = "AlertDialogFragment.PositiveButtonTitleString";
    public static final String ARG_ITEM_LIST = "AlertDialogFragment.ItemList";
    public static final String ARG_CANCELABLE = "AlertDialogFragment.ARG_CANCELABLE";

    protected int mTitleResId;
    protected int mMessageResId;
    protected int mPositiveResId;
    protected int mNeutralResId;
    protected int mNegativeResId;
    protected String mTitleString;
    protected String mMessageString;
    protected String mPositiveString;
    protected CharSequence[] mItems;
    protected DialogInterface.OnClickListener mOnClickListener;

    protected static AlertDialogFragment newInstance(Builder builder) {
        AlertDialogFragment fragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, builder.mTitleResId);
        args.putInt(ARG_MESSAGE_RES_ID, builder.mMessageResId);
        args.putInt(ARG_POSITIVE_BUTTON_RES_ID, builder.mPositiveButtonResId);
        args.putInt(ARG_NEUTRAL_BUTTON_RES_ID, builder.mNeutralButtonResId);
        args.putInt(ARG_NEGATIVE_BUTTON_RES_ID, builder.mNegativeButtonResId);
        args.putString(ARG_TITLE_STRING, builder.mTitle);
        args.putString(ARG_MESSAGE_STRING, builder.mMessage);
        args.putString(ARG_POSITIVE_BUTTON_TITLE_STRING, builder.mPositiveButtonTitle);
        args.putCharSequenceArray(ARG_ITEM_LIST, builder.mItems);
        args.putBoolean(ARG_CANCELABLE, builder.mCancelable);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }

        mTitleResId = args.getInt(ARG_TITLE_RES_ID, 0);

        mMessageResId = args.getInt(ARG_MESSAGE_RES_ID, 0);
        mPositiveResId = args.getInt(ARG_POSITIVE_BUTTON_RES_ID, 0);
        mNeutralResId = args.getInt(ARG_NEUTRAL_BUTTON_RES_ID, 0);
        mNegativeResId = args.getInt(ARG_NEGATIVE_BUTTON_RES_ID, 0);

        mTitleString = args.getString(ARG_TITLE_STRING);

        mMessageResId = args.getInt(ARG_MESSAGE_RES_ID, 0);
        mMessageString = args.getString(ARG_MESSAGE_STRING);

        mPositiveResId = args.getInt(ARG_POSITIVE_BUTTON_RES_ID, 0);
        mPositiveString = args.getString(ARG_POSITIVE_BUTTON_TITLE_STRING);

        mNegativeResId = args.getInt(ARG_NEGATIVE_BUTTON_RES_ID, 0);

        mItems = args.getCharSequenceArray(ARG_ITEM_LIST);

        setCancelable(args.getBoolean(ARG_CANCELABLE));
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        String title;
        if (mTitleResId > 0) {
            title = getString(mTitleResId);
        } else {
            title = mTitleString;
        }
        if (!TextUtils.isEmpty(title)) {
            alertDialogBuilder.setTitle(title);
        }

        String message = null;
        if (mMessageResId > 0) {
            message = getString(mMessageResId);
        } else {
            message = mMessageString;
        }
        if (!TextUtils.isEmpty(message)) {
            alertDialogBuilder.setMessage(message);
        }

        View view = getDialogView();
        if (view != null) {
            alertDialogBuilder.setView(view);
        }

        mOnClickListener = getOnClickListener();

        if (mItems != null) {
            alertDialogBuilder.setItems(mItems, mOnClickListener);
        }

        String positiveString;
        if (mPositiveResId > 0) {
            positiveString = getString(mPositiveResId);
        } else {
            positiveString = mPositiveString;
        }
        alertDialogBuilder.setPositiveButton(positiveString, mOnClickListener);

        if (mNeutralResId > 0) {
            alertDialogBuilder.setNeutralButton(mNeutralResId, mOnClickListener);
        }

        if (mNegativeResId > 0) {
            alertDialogBuilder.setNegativeButton(mNegativeResId, mOnClickListener);
        }

        return alertDialogBuilder.create();
    }

    protected View getDialogView() {
        return null;
    }

    /** responds via onActivityResult **/
    protected void sendResult(int which) {
        Fragment fragment = getTargetFragment();

        if (fragment == null) {
            sendResultToActivity(which);
            return;
        }

        Intent data = createResultData(which);
        int resultCode = which;
        fragment.onActivityResult(getTargetRequestCode(), resultCode, data);
    }

    protected Intent createResultData(int which) {
        Intent data = new Intent();
        data.putExtra(EXTRA_WHICH, which);
        return data;
    }

    /** responds via callbacks **/
    protected void sendResultToActivity(int which) {
        Activity activity = getActivity();
        if (!(activity instanceof DialogResultCallbacks)) {
            return;
        }
        DialogResultCallbacks callback = (DialogResultCallbacks) activity;

        if (which == DialogInterface.BUTTON_POSITIVE) {
            callback.onDialogPositiveResult();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            callback.onDialogNegativeResult();
        }
    }

    protected DialogInterface.OnClickListener getOnClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendResult(which);
            }
        };
    }

    public interface DialogResultCallbacks {
        public void onDialogPositiveResult();
        public void onDialogNeutralResult();
        public void onDialogNegativeResult();
    }

    public static class Builder {

        int mTitleResId;
        int mMessageResId;
        int mPositiveButtonResId;
        int mNeutralButtonResId;
        int mNegativeButtonResId;
        String mTitle;
        String mMessage;
        String mPositiveButtonTitle;
        CharSequence[] mItems;
        private boolean mCancelable;

        public Builder setTitleResId(int titleResId) {
            mTitleResId = titleResId;
            return this;
        }

        public Builder setMessageResId(int messageResId) {
            mMessageResId = messageResId;

            return this;
        }

        public Builder setPositiveButtonResId(int positiveButtonResId) {
            mPositiveButtonResId = positiveButtonResId;
            return this;
        }

        public Builder setNeutralButtonResId(int neutralButtonResId) {
            mNeutralButtonResId = neutralButtonResId;
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

        public Builder setMessage(String message) {
            mMessage = message;
            return this;
        }

        public Builder setPositiveButtonTitle(String buttonTitle) {
            mPositiveButtonTitle = buttonTitle;
            return this;
        }

        public Builder setItems(CharSequence[] items) {
            mItems = items;
            return this;
        }

        public Builder setItems(ArrayList<String> items) {
            mItems = items.toArray(new CharSequence[items.size()]);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public AlertDialogFragment build() {
            return AlertDialogFragment.newInstance(this);
        }
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public void setTitleResId(int titleResId) {
        mTitleResId = titleResId;
    }

    public int getMessageResId() {
        return mMessageResId;
    }

    public void setMessageResId(int messageResId) {
        mMessageResId = messageResId;
    }

    public int getPositiveResId() {
        return mPositiveResId;
    }

    public void setPositiveResId(int positiveResId) {
        mPositiveResId = positiveResId;
    }

    public void setNeutralResId(int neutralResId) {
        mNeutralResId = neutralResId;
    }

    public int getNeutralResId() {
        return mNeutralResId;
    }

    public String getTitleString() {
        return mTitleString;
    }

    public void setTitleString(String titleString) {
        mTitleString = titleString;
    }

    public String getMessageString() {
        return mMessageString;
    }

    public void setMessageString(String messageString) {
        mMessageString = messageString;
    }

    public int getNegativeResId() {
        return mNegativeResId;
    }

    public void setNegativeResId(int negativeResId) {
        mNegativeResId = negativeResId;
    }

    public CharSequence[] getItems() {
        return mItems;
    }

}
