package com.kelsonprime.lockitron;
import java.util.ArrayList;

public class User {

    public static ArrayList<Lock> locks() {
        ArrayList<Lock> list = new ArrayList<Lock>();
        list.add(Lock.fake());
        list.add(Lock.fake());
        return list;
    }
}
