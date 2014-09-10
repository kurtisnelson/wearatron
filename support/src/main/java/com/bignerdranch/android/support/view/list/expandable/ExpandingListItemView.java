package com.bignerdranch.android.support.view.list.expandable;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bignerdranch.android.support.data.model.ExpandableListItem;

import butterknife.ButterKnife;

public abstract class ExpandingListItemView extends LinearLayout {

    public ExpandingListItemView(Context context) {
        this(context, null);
    }

    public ExpandingListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ListView.LayoutParams params = new ListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        setOrientation(VERTICAL);

        init();
    }

    protected void init() {
        View view = LayoutInflater.from(getContext()).inflate(getLayoutResId(), this);
        ButterKnife.inject(this, view);
    }

    public void setCollapsedHeight(int collapsedHeight) {
        ViewGroup collapsedLayout = getCollapsedLayout();
        if (collapsedLayout == null) {
            return;
        }
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, collapsedHeight);
        collapsedLayout.setLayoutParams(linearLayoutParams);
    }

    public void setExpandedHeight(int expandedHeight) {
        ExpandingLayout expandingLayout = getExpandingLayout();
        if (expandingLayout == null) {
            return;
        }
        expandingLayout.setExpandedHeight(expandedHeight);
    }

    public void setSizeChangedListener(ExpandableListItem expandableListItem) {
        ExpandingLayout expandingLayout = getExpandingLayout();
        if (expandingLayout == null) {
            return;
        }
        expandingLayout.setSizeChangedListener(expandableListItem);
    }

    public void setExpanded(boolean expanded) {
        ExpandingLayout expandingLayout = getExpandingLayout();
        if (expandingLayout == null) {
            return;
        }

        if (expanded) {
            expandingLayout.setVisibility(View.VISIBLE);
        } else {
            expandingLayout.setVisibility(View.GONE);
        }
    }

    protected abstract int getLayoutResId();

    protected abstract ViewGroup getCollapsedLayout();

    protected abstract ExpandingLayout getExpandingLayout();

}
