package com.thisisnotajoke.wearatron.wear;

import com.thisisnotajoke.lockitron.WearatronApplication;

public class WearApplication extends WearatronApplication {
    protected WearModule getModule() {
        return new WearModule(this);
    }
}