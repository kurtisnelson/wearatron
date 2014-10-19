package com.thisisnotajoke.wearatron;

import com.thisisnotajoke.lockitron.WearatronApplication;

public class MobileApplication extends WearatronApplication {
    protected MobileModule getModule() {
        return new MobileModule(this);
    }
}
