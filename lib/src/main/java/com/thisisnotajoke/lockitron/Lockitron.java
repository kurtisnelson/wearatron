package com.thisisnotajoke.lockitron;

import android.content.Context;

public class Lockitron {
    public static final String ENDPOINT = "https://api.lockitron.com/v1/";
    private final Context context;

    public Lockitron(Context c){
        this.context = c;
    }
    public User user(String token) {
        return new User(context, token);
    }

}
