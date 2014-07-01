package com.thisisnotajoke.lockitron;

public class Lock {
    private final String uuid;
    private final String name;

    public Lock(String uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
