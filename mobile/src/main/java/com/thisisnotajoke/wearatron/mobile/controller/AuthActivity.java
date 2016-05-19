package com.thisisnotajoke.wearatron.mobile.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.thisisnotajoke.lockitron.AuthenticationService;
import com.thisisnotajoke.lockitron.model.PreferenceManager;
import com.thisisnotajoke.lockitron.controller.WearatronActivity;
import com.thisisnotajoke.wearatron.mobile.R;

import org.scribe.model.Token;

import javax.inject.Inject;

public class AuthActivity extends WearatronActivity implements AuthenticationService.TokenCallback {
    @Inject
    PreferenceManager mPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Token token = mPreferenceManager.getToken();
        if(token != null){
            success();
        }else {
            AuthenticationService auth = new AuthenticationService(this);
            findViewById(R.id.activity_auth_progress).setVisibility(View.GONE);
            WebView webView = (WebView) findViewById(R.id.activity_auth_webview);
            webView.setVisibility(View.VISIBLE);
            auth.getToken(this, webView);
        }
    }

    @Override
    public void token(Token token) {
        mPreferenceManager.setToken(token);
        success();
    }

    public void success() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected boolean usesInjection() {
        return true;
    }
}
