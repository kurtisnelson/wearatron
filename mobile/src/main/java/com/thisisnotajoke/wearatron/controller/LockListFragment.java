package com.thisisnotajoke.wearatron.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.controller.WearatronFragment;
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.lockitron.model.event.LockUpdatedEvent;
import com.thisisnotajoke.wearatron.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LockListFragment extends WearatronFragment implements AdapterView.OnItemClickListener {
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_LOCK = "LOCK.UUID";
    private static final String TAG = "LockListFragment";
    private Callbacks mCallbacks;
    private ListView mListView;
    private String mSelectedUuid;

    @Inject
    protected DataManager mDataManager;
    private LockAdapter mAdapter;

    public static LockListFragment newInstance(String token, Lock selectedLock) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TOKEN, token);
        if(selectedLock != null)
            args.putString(EXTRA_LOCK, selectedLock.getUUID());
        LockListFragment fragment = new LockListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedUuid = getArguments().getString(EXTRA_LOCK);
        mDataManager.loadLocks();
        mAdapter = new LockAdapter(new ArrayList<Lock>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lock_list, container, false);
        mListView = (ListView) v.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        return v;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Lock lock = mAdapter.getItem(position);
        mSelectedUuid = lock.getUUID();
        mListView.setItemChecked(position, true);
        mCallbacks.onLockSelected(lock);
    }

    public interface Callbacks {
        void onLockSelected(Lock lock);
    }



    public void onEventMainThread(LockUpdatedEvent e) {
        mAdapter = new LockAdapter(mDataManager.getMyLocks());
        mListView.setAdapter(mAdapter);
    }

    private class LockAdapter extends ArrayAdapter<Lock> {
        public LockAdapter(List<Lock> locks){
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

    @Override
    protected boolean usesInjection() {
        return true;
    }

    @Override
    protected boolean registerForEvents() {
        return true;
    }
}
