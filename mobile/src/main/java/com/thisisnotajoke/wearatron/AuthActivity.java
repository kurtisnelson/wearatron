package com.thisisnotajoke.wearatron;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;

import com.thisisnotajoke.lockitron.Auth;
import com.thisisnotajoke.lockitron.PreferenceManager;

import org.scribe.model.Token;

public class AuthActivity extends FragmentActivity implements Auth.TokenCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Token token = new PreferenceManager(this).getToken();
        if(token != null){
            success();
        }else {
            Auth auth = new Auth(this);
            findViewById(R.id.activitY_auth_progress).setVisibility(View.GONE);
            WebView webView = (WebView) findViewById(R.id.activity_auth_webview);
            webView.setVisibility(View.VISIBLE);
            auth.getToken(this, webView);
        }
    }

    @Override
    public void token(Token token) {
        new PreferenceManager(this).setToken(token);
        success();
    }

    public void success() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
