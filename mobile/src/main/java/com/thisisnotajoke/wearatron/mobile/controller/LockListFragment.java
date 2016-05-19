package com.thisisnotajoke.wearatron.mobile.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.lockitron.controller.WearatronFragment;
import com.thisisnotajoke.lockitron.model.DataManager;
import com.thisisnotajoke.lockitron.model.event.LockUpdatedEvent;
import com.thisisnotajoke.wearatron.mobile.R;
import com.thisisnotajoke.wearatron.mobile.util.RecyclerItemClickListener;
import com.thisisnotajoke.wearatron.mobile.view.DividerItemDecoration;
import com.thisisnotajoke.wearatron.mobile.view.LockViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LockListFragment extends WearatronFragment implements RecyclerItemClickListener.OnItemClickListener {
    private static final String EXTRA_TOKEN = "TOKEN";
    private static final String EXTRA_LOCK = "LOCK.UUID";
    private static final String TAG = "LockListFragment";
    private Callbacks mCallbacks;
    private String mSelectedUuid;

    @Inject
    protected DataManager mDataManager;
    private LockViewAdapter mAdapter;

    static LockListFragment newInstance(String token, Lock selectedLock) {
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
        mAdapter = new LockViewAdapter(new ArrayList<Lock>());
        FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.APP_OPEN, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lock_list, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_lock_list_list);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
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
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick");
        Lock lock = mAdapter.getItem(position);
        mAdapter.clearSelections();
        mAdapter.setSelection(position, true);
        mSelectedUuid = lock.getUUID();
        mCallbacks.onLockSelected(lock);
    }

    public interface Callbacks {
        void onLockSelected(Lock lock);
    }

    public void onEventMainThread(LockUpdatedEvent e) {
        List<Lock> locks = mDataManager.getMyLocks();
        mAdapter.setData(locks);
        if(mSelectedUuid != null) {
            for(int i = 0; i < locks.size(); i++) {
                if(locks.get(i).getUUID().equals(mSelectedUuid))
                    mAdapter.setSelection(i, true);
            }
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
