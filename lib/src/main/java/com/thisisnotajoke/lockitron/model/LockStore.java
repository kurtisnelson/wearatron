package com.thisisnotajoke.lockitron.model;

import com.thisisnotajoke.lockitron.Lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockStore {
    private Map<String,Lock> mLocks;

    public LockStore() {
        mLocks = new HashMap<String, Lock>();
    }

    public void putLock(Lock lock) {
        mLocks.put(lock.getUUID(), lock);
    }

    public Lock getLock(String uuid) {
        return mLocks.get(uuid);
    }

    public List<Lock> all() {
        return new ArrayList<Lock>(mLocks.values());
    }
}
