package com.thisisnotajoke.wearatron;

import com.bignerdranch.android.support.BaseApplication;

public class MobileApplication extends BaseApplication {
    @Override
    protected MobileModule getModule() {
        return new MobileModule(this);
    }
}
