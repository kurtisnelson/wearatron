package com.thisisnotajoke.wearatron;

import android.app.Activity;
import android.os.Bundle;

public class LockitronActivity extends Activity {
    private static final int PROMPT_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationDecorator.notify(this, NotificationDecorator.Type.PUSH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        finish();
    }
}