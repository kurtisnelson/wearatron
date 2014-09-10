/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bignerdranch.android.support.data.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bignerdranch.android.support.data.model.ExpandableListItem;
import com.bignerdranch.android.support.view.list.expandable.ExpandingListItemView;

import java.util.List;

/**
 * This is a custom array adapter used to populate the listview whose items will
 * expand to display extra content in addition to the default display.
 */
public abstract class ExpandingListViewArrayAdapter extends ArrayAdapter<ExpandableListItem> {

    public ExpandingListViewArrayAdapter(Context context, int layoutViewResourceId,
                                         List<ExpandableListItem> data) {
        super(context, layoutViewResourceId, data);
    }

    /**
     * This method updates the layout parameters of the item's view so that the collapsed content is
     * centered in the bounds of the collapsed view, and such that the expanding content is not
     * displayed in the collapsed state of the cell.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ExpandableListItem listItem = getItem(position);
        ExpandingListItemView listItemView = (ExpandingListItemView) convertView;

        if (listItemView == null) {
            listItemView = getExpandingListItemView(getItemViewType(position));
        }

        listItemView.setCollapsedHeight(listItem.getCollapsedHeight());
        listItemView.setExpandedHeight(listItem.getExpandedHeight());
        listItemView.setSizeChangedListener(listItem);

        boolean expanded = listItem.isExpanded();
        listItemView.setExpanded(expanded);

        return listItemView;
    }

    protected abstract ExpandingListItemView getExpandingListItemView(int type);
}