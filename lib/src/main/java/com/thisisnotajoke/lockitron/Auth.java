package com.thisisnotajoke.lockitron;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kelsonprime.lockitron.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class Auth {
    private static final String PREF_STORE = "LockitronOauth";
    public static final String REDIRECT_URI	= "http://localhost";
    private static final String TAG = "Auth";
    private OAuthService mService;
    private TokenCallback mCallback;

    public interface TokenCallback {
        public void token(Token token);
    }

    public Auth(Context context) {
        mService = new ServiceBuilder()
                .provider(LockitronApi.class)
                .apiKey(context.getString(R.string.oauth_id))
                .apiSecret(context.getString(R.string.oauth_secret))
                .signatureType(SignatureType.QueryString)
                .callback(REDIRECT_URI)
                .build();
    }

    public void getToken(TokenCallback cb, WebView webView) {
        mCallback = cb;
        driveWebview(webView);
    }

    public void driveWebview(final WebView webView) {
        String authUrl = mService.getAuthorizationUrl(null);
        Log.d(TAG, authUrl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "** in shouldOverrideUrlLoading(), url is: " + url);
                if( url.startsWith(Auth.REDIRECT_URI) ) {
                    Log.d(TAG, "Overridng loading");
                    // extract OAuth2 access_token appended in url
                    String code = Uri.parse(url).getQueryParameter("code");
                    verify(code);
                    // don't go to redirectUri
                    return true;
                }

                // load the webpage from url (login and grant access)
                return super.shouldOverrideUrlLoading(view, url); // return false;
            }
        });
    }

    private void verify(String token) {
        new AsyncTask<String, Void, Token>() {

            @Override
            protected Token doInBackground(String... params) {
                Verifier verifier = new Verifier(params[0]);
                return mService.getAccessToken(null, verifier);
            }

            @Override
            protected void onPostExecute(Token token) {
                super.onPostExecute(token);
                mCallback.token(token);
            }
        }.execute(token);
    }
}
