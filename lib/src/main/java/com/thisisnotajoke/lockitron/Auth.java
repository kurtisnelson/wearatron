package com.thisisnotajoke.lockitron;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.kelsonprime.lockitron.R;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;

public class Auth {
    private static final String PREF_STORE = "LockitronOauth";
    private com.google.api.client.auth.oauth2.Credential mCredential;
    private String mUserId;
    private AuthorizationFlow mFlow;
    private SharedPreferencesCredentialStore mCredentialStore;

    public Auth(Context context, String userId) {
        mUserId = userId;
        mCredentialStore = new SharedPreferencesCredentialStore(context, PREF_STORE, new JacksonFactory());
        try {
            mCredentialStore.load(userId, mCredential);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authenticate(FragmentActivity activity) {
        buildFlow(activity.getResources());
        AuthorizationUIController controller =
                new DialogFragmentController(activity.getSupportFragmentManager()) {

                    @Override
                    public String getRedirectUri() throws IOException {
                        return "http://localhost/Callback";
                    }

                    @Override
                    public boolean isJavascriptEnabledForWebView() {
                        return true;
                    }

                };

        OAuthManager oauth = new OAuthManager(mFlow, controller);
        try {
            mCredential = oauth.authorizeImplicitly(mUserId, null, null).getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken(){
        if(mCredential == null){
            return null;
        }
        return mCredential.getAccessToken();
    }

    private void buildFlow(Resources res){
        AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                new JacksonFactory(),
                new GenericUrl(res.getString(R.string.token_endpoint)),
                new ClientParametersAuthentication(res.getString(R.string.oauth_id), res.getString(R.string.oauth_secret)),
                res.getString(R.string.oauth_id),
                res.getString(R.string.auth_endpoint));
        builder.setCredentialStore(mCredentialStore);
        mFlow = builder.build();
    }
}
