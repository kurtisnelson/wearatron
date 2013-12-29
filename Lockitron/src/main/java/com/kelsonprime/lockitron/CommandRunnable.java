package com.kelsonprime.lockitron;

import android.app.Service;

public class CommandRunnable implements Runnable {
    private final Service service;
    private final int startId;
    private final boolean unlock;
    private final String LOCK_UUID = "0433dabf-dd78-4599-a9cb-5cb8258f5df0";
    private final String TOKEN = "";

    public CommandRunnable(int startId, Service service, boolean unlock){
        this.service = service;
        this.startId = startId;
        this.unlock = unlock;
    }

    @Override
    public void run() {


    }
}
