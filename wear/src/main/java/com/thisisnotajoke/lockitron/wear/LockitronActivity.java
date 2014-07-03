package com.thisisnotajoke.lockitron.wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.ImageReference;

public class LockitronActivity extends InsetActivity{
    private GridViewPager mDoorPager;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    public void onReadyForContent() {
        setContentView(R.layout.activity_lockitron);
        mDoorPager = (GridViewPager) findViewById(R.id.activity_lockitron_pager);
        mDoorPager.setBackgroundColor(getResources().getColor(R.color.blue));
        mDoorPager.setAdapter(new DoorGridViewPagerAdapter(getFragmentManager()));
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
            return CircleFragment.newInstance(column % 2 == 0);
        }

        @Override
        protected long getFragmentId(int row, int column) {
            return row * column;
        }
    }

}

