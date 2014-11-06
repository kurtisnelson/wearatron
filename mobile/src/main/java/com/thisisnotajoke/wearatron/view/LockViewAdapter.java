package com.thisisnotajoke.wearatron.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelsonprime.lockitron.R;
import com.thisisnotajoke.lockitron.Lock;
import com.thisisnotajoke.wearatron.util.RecyclerViewSelectAdapter;

import java.util.List;

public class LockViewAdapter extends RecyclerViewSelectAdapter<LockViewAdapter.LockViewHolder> {
    private List<Lock> mLocks;

    public LockViewAdapter(List<Lock> locks) {
        super();
        mLocks = locks;
    }

    @Override
    public LockViewAdapter.LockViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_lock, viewGroup, false);
        return new LockViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(LockViewAdapter.LockViewHolder listItemViewHolder, int position) {
        Lock lock = mLocks.get(position);
        listItemViewHolder.setName(lock.getName());
        listItemViewHolder.setActivated(isSelected(position));
    }

    @Override
    public int getItemCount() {
        return mLocks.size();
    }

    public Lock getItem(int position) {
        return mLocks.get(position);
    }

    public void insert(Lock lock) {
        mLocks.add(lock);
        notifyItemInserted(mLocks.size() - 1);
    }

    public void setData(List<Lock> list) {
        mLocks = list;
        notifyDataSetChanged();
    }

    public final static class LockViewHolder extends RecyclerView.ViewHolder {
        private final TextView mLabel;

        public LockViewHolder(View itemView, int viewType) {
            super(itemView);
            mLabel = (TextView) itemView.findViewById(R.id.list_item_lock_name);
        }

        public void setName(String name) {
            mLabel.setText(name);
        }

        public void setActivated(boolean selected) {
            itemView.setActivated(selected);
        }
    }
}
