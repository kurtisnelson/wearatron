package com.thisisnotajoke.lockitron.model;

import com.thisisnotajoke.lockitron.Lock;

import org.scribe.model.Token;

import java.util.List;

public interface DataManager {
    public void lockMyLock();
    public void unlockMyLock();

    public void loadLocks();
    public List<Lock> getMyLocks();

    public Token getToken();

    public Lock getActiveLock();

    public void setActiveLock(Lock lock);
}
