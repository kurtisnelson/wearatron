package com.kelsonprime.lockitron.glass;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kelsonprime.lockitron.Lockitron;
import com.kelsonprime.lockitron.Lock;

import java.util.ArrayList;

public class LockListFragment extends ListFragment {
    private static final String EXTRA_TOKEN = "TOKEN";
    private String token;
    private ArrayList<Lock> mLocks;
    private Callbacks mCallbacks;

    public static LockListFragment newInstance(String token) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TOKEN, token);

        LockListFragment fragment = new LockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Callbacks {
        void onLockSelected(Lock lock);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = getArguments().getString(EXTRA_TOKEN);
        mLocks = Lockitron.user(token).locks();
        LockAdapter adapter = new LockAdapter(mLocks);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Lock lock = ((LockAdapter) getListAdapter()).getItem(position);
        mCallbacks.onLockSelected(lock);
    }

    private class LockAdapter extends ArrayAdapter<Lock> {
        public LockAdapter(ArrayList<Lock> locks){
            super(getActivity(), android.R.layout.simple_list_item_1, locks);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(null == convertView){
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_lock, null);
            }

            Lock l = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.lock_list_item_nameTextView);
            titleTextView.setText(l.getName());
            return convertView;
        }
    }
}
