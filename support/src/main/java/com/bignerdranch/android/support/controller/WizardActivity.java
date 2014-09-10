package com.bignerdranch.android.support.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bignerdranch.android.support.R;
import com.bignerdranch.android.support.controller.AlertDialogFragment.DialogResultCallbacks;
import com.bignerdranch.android.support.view.WizardProgressView;
import com.bignerdranch.android.support.view.WizardViewPager;

import java.lang.ref.WeakReference;

public abstract class WizardActivity extends BaseActivity implements DialogResultCallbacks {
    private static final String TAG_ALERT_DIALOG = "TAG_ALERT_DIALOG";
    private static final String EXTRA_NEXT_ENABLED = "NextEnabled";
    private static final String EXTRA_NEXT_TEXT = "NextText";
    private static final String EXTRA_PREVIOUS_TEXT = "PreviousText";
    private static final String EXTRA_PREVIOUS_ENABLED = "PreviousEnabled";

    protected WizardViewPager mViewPager;
    protected WizardProgressView mWizardProgressView;
    protected WizardFragmentAdapter mAdapter;
    private Button mNext;
    private Button mPrevious;
    private ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayout());

        initView();

        if (savedInstanceState != null) {
            mNext.setEnabled(savedInstanceState.getBoolean(EXTRA_NEXT_ENABLED, false));
            mNext.setText(savedInstanceState.getString(EXTRA_NEXT_TEXT));
            mPrevious.setEnabled(savedInstanceState.getBoolean(EXTRA_PREVIOUS_ENABLED, false));
            mPrevious.setText(savedInstanceState.getString(EXTRA_PREVIOUS_TEXT));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_NEXT_ENABLED, mNext.isEnabled());
        outState.putBoolean(EXTRA_PREVIOUS_ENABLED, mPrevious.isEnabled());
        outState.putString(EXTRA_NEXT_TEXT, mNext.getText().toString());
        outState.putString(EXTRA_PREVIOUS_TEXT, mPrevious.getText().toString());
    }

    protected void initView() {
        mWizardProgressView = (WizardProgressView) findViewById(R.id.wizard_indicator);
        mViewPager = (WizardViewPager) findViewById(R.id.wizard_viewpager);

        setupButtons();

        setupPager();

        updateUI();
    }

    private void setupButtons() {
        mPrevious = (Button) findViewById(R.id.wizard_previous);
        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        mNext = (Button) findViewById(R.id.wizard_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private void setupPager() {
        mAdapter = newAdapter();
        mWizardProgressView.setCount(mAdapter.getCount());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new WizardOnPageChangeListener());
    }

    protected void showLoading() {
        showLoading(R.string.loading_title, R.string.loading_message);
    }

    protected void showLoading(@StringRes int titleResId, @StringRes int messageResId) {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(titleResId);
        mProgress.setMessage(getText(messageResId));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();
    }

    protected void hideLoading() {
        if(mProgress != null){
            mProgress.cancel();
            mProgress = null;
        }
    }

    protected abstract int getContentLayout();

    protected void setNextEnabled(boolean b) {
        mNext.setEnabled(b);
    }

    protected void next() {
        if (currentFragment().next()) {
            int i = mViewPager.getCurrentItem();
            if (i < mAdapter.getCount() - 1) {
                mViewPager.setCurrentItem(i + 1, true);
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    protected void previous() {
        if (currentFragment().previous()) {
            int i = mViewPager.getCurrentItem();
            if (i > 0) {
                mViewPager.setCurrentItem(i - 1, true);
            }
        }
    }

    @Override
    public String getActionBarTitle() {
        WizardFragment fragment = currentFragment();
        if (fragment != null) {
            return getString(fragment.getTitleResId());
        }
        return "";
    }

    public abstract WizardFragmentAdapter newAdapter();


    private void updateUI() {
        int position = mViewPager.getCurrentItem();
        mWizardProgressView.setStep(position);

        setActionBarTitle();

        syncButtons();
    }

    protected void syncButtons() {
        WizardFragment currentFragment = currentFragment();
        if (currentFragment == null) {
            return;
        }

        mPrevious.setVisibility(currentFragment.hasPrevious() ? View.VISIBLE : View.GONE);
        mPrevious.setText(currentFragment.previousText());

        mNext.setVisibility(currentFragment.hasNext() ? View.VISIBLE : View.GONE);
        mNext.setText(currentFragment.nextText());
        mNext.setEnabled(currentFragment.isNextEnabled());
    }

    protected WizardFragment currentFragment() {
        if (mViewPager != null) {
            return mAdapter.getCurrentFragment();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        displayExitConfirmationDialog();
    }

    protected void displayExitConfirmationDialog() {
        AlertDialogFragment dialog = new AlertDialogFragment.Builder()
                .setTitleResId(getExitConfirmationDialogTitleResId())
                .setMessageResId(getExitConfirmationDialogMessageResId())
                .setPositiveButtonResId(getExitConfirmationDialogPositiveButtonResId())
                .setNegativeButtonResId(getExitConfirmationDialogNegativeButtonResId())
                .build();
        dialog.show(getSupportFragmentManager(), TAG_ALERT_DIALOG);
    }

    @StringRes
    protected abstract int getExitConfirmationDialogTitleResId();

    @StringRes
    protected abstract int getExitConfirmationDialogMessageResId();

    @StringRes
    protected abstract int getExitConfirmationDialogPositiveButtonResId();

    @StringRes
    protected abstract int getExitConfirmationDialogNegativeButtonResId();

    @Override
    public void onDialogPositiveResult() {
        finish();
    }

    @Override
    public void onDialogNeutralResult() {
        // do nothing, no neutral button
    }

    @Override
    public void onDialogNegativeResult() {
        //do nothing
    }

    protected class WizardOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mAdapter != null && mAdapter.getCurrentFragment() != null) {
                mAdapter.getCurrentFragment().onCurrent();
            }
            updateUI();
        }
    }

    public static abstract class WizardFragmentAdapter extends FragmentPagerAdapter {

        private WeakReference<WizardFragment> mCurrentFragmentRef;

        public WizardFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragmentRef = new WeakReference<WizardFragment>((WizardFragment)object);
        }

        public abstract WizardFragment getItem(int position);

        public WizardFragment getCurrentFragment() {
            if (mCurrentFragmentRef != null) {
                return mCurrentFragmentRef.get();
            }
            return null;
        }
    }
}