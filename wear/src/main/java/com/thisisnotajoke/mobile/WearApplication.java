package com.thisisnotajoke.mobile;

import com.thisisnotajoke.lockitron.WearatronApplication;

public class WearApplication extends WearatronApplication {
    protected WearModule getModule() {
        return new WearModule(this);
    }
}