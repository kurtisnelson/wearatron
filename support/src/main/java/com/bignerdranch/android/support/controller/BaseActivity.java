package com.bignerdranch.android.support.controller;

import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.support.AppConstants;
import com.bignerdranch.android.support.R;
import com.bignerdranch.android.support.data.event.ConnectivityEvent;
import com.bignerdranch.android.support.data.event.EventHelper;
import com.bignerdranch.android.support.util.ConnectivityReceiver;
import com.bignerdranch.android.support.util.InjectionUtils;
import com.bignerdranch.android.support.util.NetworkConnectivityManager;

public class BaseActivity extends FragmentActivity {

    private final String TAG = AppConstants.APP_TAG + this.getClass().getSimpleName();
    private ConnectivityReceiver mConnectivityReceiver;
    private TextView mConnectivityView;

/* Lifecycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (usesInjection()) {
            InjectionUtils.injectClass(this);

            if (registerForEvents()) {
                EventHelper.registerSubscriber(this);
            }
        }

//        disabled until font is chosen
//        CalligraphyConfig.initDefault(FontUtils.BASE_TYPEFACE);

        setActionBarTitle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnectivityReceiver = new ConnectivityReceiver();
        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if(mConnectivityView != null) {
            NetworkConnectivityManager ncm = new NetworkConnectivityManager(this);
            if (!ncm.isConnected()) {
                mConnectivityView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mConnectivityReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usesInjection() && registerForEvents()) {
            EventHelper.unregisterSubscriber(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasParentActivity()) {
                    navigateUp();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }


    protected void enableConnectivityAlert() {
        FrameLayout root = (FrameLayout) findViewById(android.R.id.content);
        mConnectivityView = new TextView(this);
        mConnectivityView.setText(R.string.network_failure);
        mConnectivityView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        mConnectivityView.setTextColor(getResources().getColor(android.R.color.white));
        mConnectivityView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mConnectivityView.setGravity(Gravity.CENTER);
        int padding = getResources().getDimensionPixelSize(R.dimen.connectivity_padding);
        mConnectivityView.setPadding(padding, padding, padding, padding);
        mConnectivityView.setVisibility(View.GONE);

        addContentView(mConnectivityView, mConnectivityView.getLayoutParams());
    }

    public void onEventMainThread(ConnectivityEvent e) {
        if(mConnectivityView == null)
            return;
        int visibility = e.isConnected() ? View.GONE : View.VISIBLE;
        mConnectivityView.setVisibility(visibility);
    }

    // disabled until font is chosen
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
//    }

    protected void toast(int resId) {
        toast(getString(resId));
    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /* ActionBar */

    protected void setActionBarTitle() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setLogo(new ColorDrawable(android.R.color.transparent));
        actionBar.setDisplayShowTitleEnabled(true);
        String title = getActionBarTitle();
        if (!TextUtils.isEmpty(title)) {
            title = getActionBarTitle().toUpperCase();
            if (usingCustomFont()) {
//                SpannableString spannableString = new SpannableString(title);
////                Typeface typeface = FontUtils.getSemiBoldTypeface(this);
//                Typeface typeface = Typeface.createFromAsset(getAssets(), AppConstants.ACTION_BAR_TYPEFACE);
//                int textSize = (int) getResources().getDimension(R.dimen.actionbar_text_size);
//                spannableString.setSpan(new TypefaceSpan(typeface, textSize), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                actionBar.setTitle(spannableString);
            } else {
                actionBar.setTitle(title);
            }
        }

        // decide to display home caret
        if (hasParentActivity() && shouldNavigateToParentActivity()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public String getActionBarTitle() {
        return (String) getTitle();
    }

    /* Navigation */

    public void navigateUp() {
        Intent parentIntent = getNavParentActivityIntent();
        NavUtils.navigateUpTo(this, parentIntent);
    }

    protected Intent getNavParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    private boolean hasParentActivity() {
        return NavUtils.getParentActivityIntent(this) != null;
    }

    protected boolean shouldNavigateToParentActivity() {
        return true;
    }

    /* Injection */

    // Override this in subclasses in order to turn on injection
    protected boolean usesInjection() {
        return false;
    }

     /* Events */

    // override to be registered for events
    // onEvent() will be required to avoid errors
    protected boolean registerForEvents() {
        return true;
    }

    /* Custom Fonts*/

    /**
     * If a custom font is being used, override to enable it in the action bar
     *
     * @return true if using a custom font
     */
    protected boolean usingCustomFont() {
        return false;
    }
}
