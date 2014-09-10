package com.bignerdranch.android.support.controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.support.R;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

public abstract class WizardFragment extends BaseFragment implements Validator.ValidationListener, View.OnFocusChangeListener, TextWatcher {
    private static final java.lang.String ARG_IS_NEXT_ENABLED = "IsNextEnabled";
    private static final long VALIDATION_DELAY = 1000;

    private WizardActivity mWizardActivity;
    private Validator mValidator;
    protected boolean mNextEnabled;
    private Handler mValidationHandler;

    private Runnable mValidationRunnable = new Runnable() {
        @Override
        public void run() {
            runValidation();
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mWizardActivity = (WizardActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mValidator.cancelAsync();
        mWizardActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValidationHandler = new Handler();
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mNextEnabled = false;
        if (savedInstanceState != null) {
            mNextEnabled = savedInstanceState.getBoolean(ARG_IS_NEXT_ENABLED, false);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        runValidation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_IS_NEXT_ENABLED, mNextEnabled);
    }

    @StringRes
    public abstract int getTitleResId();

    public boolean hasNext() {
        return true;
    }

    @StringRes
    public int nextText() {
        return R.string.next;
    }

    public boolean hasPrevious() {
        return true;
    }

    @StringRes
    public int previousText() {
        return R.string.previous;
    }

    public boolean next() {
        return true;
    }

    public boolean previous() {
        return true;
    }

    protected void runValidation() {
        if (mWizardActivity == null || mWizardActivity.currentFragment() != this) {
            return;
        }
        mValidator.validateAsync();
    }

    protected boolean validateSecondStage() {
        return true;
    }

    @Override
    public void onValidationSucceeded() {
        if (mWizardActivity.currentFragment() != this) {
            return;
        }
        if (!validateSecondStage()) {
            return;
        }
        mNextEnabled = true;
        mWizardActivity.syncButtons();
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        if (mWizardActivity.currentFragment() != this) {
            return;
        }
        mNextEnabled = false;
        mWizardActivity.syncButtons();
        String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            if (((EditText) failedView).getText().length() > 0) {
                ((EditText) failedView).setError(message);
            }
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    //Validation stuffs
    protected void addValidationOnFocusChange(View... views) {
        for (View view : views) {
            view.setOnFocusChangeListener(this);
        }
    }

    protected void removeValidationOnFocusChange(View... views) {
        for (View view : views) {
            view.setOnFocusChangeListener(null);
        }
    }

    protected void addValidationOnTextChange(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.addTextChangedListener(this);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            runValidation();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mValidationHandler.removeCallbacks(mValidationRunnable);
        mValidationHandler.postDelayed(mValidationRunnable, VALIDATION_DELAY);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean isNextEnabled() {
        return mNextEnabled;
    }

    public void onCurrent() {
        runValidation();
    }
}
