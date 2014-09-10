package com.thisisnotajoke.lockitron.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kelsonprime.lockitron.R;
import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.Lockitron;
import com.thisisnotajoke.lockitron.User;

import java.util.ArrayList;

public class LockListFragment extends ListFragment {
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_LOCK = "LOCK.UUID";
    private static final String TAG = "LockListFragment";
    private ArrayList<Lock> mLocks;
    private Callbacks mCallbacks;
    private ListView mListView;
    private String mSelectedUuid;
    private String mToken;

    public static LockListFragment newInstance(String token, Lock selectedLock) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TOKEN, token);
        if(selectedLock != null)
            args.putString(EXTRA_LOCK, selectedLock.getUUID());
        LockListFragment fragment = new LockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Callbacks {
        void onLockSelected(Lock lock);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = getArguments().getString(EXTRA_TOKEN);
        mSelectedUuid = getArguments().getString(EXTRA_LOCK);
        User lockitronUser = new Lockitron(getActivity().getApplicationContext()).user(mToken);
        Log.d(TAG, "My token is " + mToken);
        mLocks = lockitronUser.getLocks();
        LockAdapter adapter = new LockAdapter(mLocks);
        lockitronUser.setLocksAdapter(adapter);
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void updateUI() {
        ((LockAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lock_list, container, false);
        mListView = (ListView) v.findViewById(android.R.id.list);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Lock lock = ((LockAdapter) getListAdapter()).getItem(position);
        mSelectedUuid = lock.getUUID();
        mListView.setItemChecked(position, true);
        mCallbacks.onLockSelected(lock);
    }

    private class LockAdapter extends ArrayAdapter<Lock> {
        public LockAdapter(ArrayList<Lock> locks){
            super(getActivity(), R.layout.list_item_lock, locks);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(null == convertView){
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_lock, null);
            }

            Lock l = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.lock_list_item_nameTextView);
            titleTextView.setText(l.getName());

            if(l.getUUID().equals(mSelectedUuid)){
                mListView.setItemChecked(position, true);
            }

            return convertView;
        }
    }
}
