package com.thisisnotajoke.wearatron;

import android.app.Fragment;
import android.app.FragmentManager;

import android.os.Handler;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;

public class LockitronActivity extends InsetActivity implements CircleFragment.Callback {
    private static final String TAG = "LockitronActivity";
    public static final String DO_EXTRA = "DoExtra";
    private GridViewPager mDoorPager;

    @Override
    public void onReadyForContent() {
        setContentView(R.layout.activity_lockitron);
        mDoorPager = (GridViewPager) findViewById(R.id.activity_lockitron_pager);
        mDoorPager.setBackgroundColor(getResources().getColor(R.color.black));
        mDoorPager.setAdapter(new DoorGridViewPagerAdapter(getFragmentManager()));
        if (getIntent().hasExtra(DO_EXTRA)) {
            Runnable dirtyHack = new Runnable() {
                @Override
                public void run() {
                    boolean task = getIntent().getBooleanExtra(DO_EXTRA, false);
                    mDoorPager.setCurrentItem(0, task ? 0 : 1);
                    onClick(task);
                }
            };
            Handler handler = new Handler();
            handler.postDelayed(dirtyHack, 100);
        }
    }

    @Override
    public void onClick(boolean lock) {
        new LockMessageTask(this).execute(lock);
    }

    class DoorGridViewPagerAdapter extends FragmentGridPagerAdapter {

        public DoorGridViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 2;
        }

        @Override
        public Fragment getFragment(int row, int column) {
            CircleFragment f = CircleFragment.newInstance(column % 2 == 0);
            f.setCallback(LockitronActivity.this);
            return f;
        }

        @Override
        protected long getFragmentId(int row, int column) {
            return row * column;
        }
    }
}

