package com.thisisnotajoke.lockitron;

import android.content.Context;
import android.content.SharedPreferences;

import org.scribe.model.Token;

public class PreferenceManager {
    private static final String TOKEN_SECRET = "Secret";
    private static final String TOKEN_TOKEN = "Token";
    private final String PREF_NAME = "Lockitron";
    private final Context c;

    public PreferenceManager(Context context){
        c = context;
    }

    public void setToken(Token token){
        c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(TOKEN_SECRET, token.getSecret())
                .putString(TOKEN_TOKEN, token.getToken())
                .commit();
    }

    public Token getToken() {
        SharedPreferences prefs = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String secret = prefs.getString(TOKEN_SECRET, null);
        String token = prefs.getString(TOKEN_TOKEN, null);

        if(secret == null || token == null){
            return null;
        }
        return new Token(token, secret);
    }
}
