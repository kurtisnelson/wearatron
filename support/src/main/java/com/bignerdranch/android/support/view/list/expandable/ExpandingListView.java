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

package com.bignerdranch.android.support.view.list.expandable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bignerdranch.android.support.data.model.ExpandableListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A custom listview which supports the preview of extra content corresponding to each cell
 * by clicking on the cell to hide and show the extra content.
 */
public class ExpandingListView extends ListView {

    private boolean mShouldRemoveObserver;
    private List<View> mViewsToDraw;
    private int[] mExpandingTranslate;
    private int[] mCollapsingTranslate;
    private boolean mDisallowMultipleExpandedViews;
    private ExpandingListItemView mCurrentExpandedListItemView;

    public ExpandingListView(Context context) {
        this(context, null);
    }

    public ExpandingListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mShouldRemoveObserver = false;
        setOnItemClickListener(mItemClickListener);
        mViewsToDraw = new ArrayList<View>();
    }

    public void setDisallowMultipleExpandedViews(boolean disallowMultipleExpandedViews) {
        mDisallowMultipleExpandedViews = disallowMultipleExpandedViews;
    }

    public void onItemClick(View view, int position) {
        mItemClickListener.onItemClick(null, view, position, 0);
    }

    /**
     * Listens for item clicks and expands or collapses the selected view depending on
     * its current state.
     */
    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ExpandingListItemView listItemView = (ExpandingListItemView) view;

            int viewPosition = getPositionForView(view);
            ExpandableListItem listItem = (ExpandableListItem) getItemAtPosition(viewPosition);

            if (!listItem.isExpandable()) {
                return;
            }

            if (!listItem.isExpanded()) {
                if (mDisallowMultipleExpandedViews
                        && mCurrentExpandedListItemView != null) {
                    collapseOldViewExpandNew(mCurrentExpandedListItemView, listItemView);
                } else {
                    expandView(listItemView);
                }
            } else {
                collapseView(listItemView);
            }
        }
    };

    /**
     * Calculates the top and bottom bound changes of the selected item. These values are
     * also used to move the bounds of the items around the one that is actually being
     * expanded or collapsed.
     * <p/>
     * This method can be modified to achieve different user experiences depending
     * on how you want the cells to expand or collapse. In this specific demo, the cells
     * always try to expand downwards (leaving top bound untouched), and similarly,
     * collapse upwards (leaving top bound untouched). If the change in bounds
     * results in the complete disappearance of a cell, its lower bound is moved is
     * moved to the top of the screen so as not to hide any additional content that
     * the user has not interacted with yet. Furthermore, if the collapsed cell is
     * partially off screen when it is first clicked, it is translated such that its
     * full contents are visible. Lastly, this behaviour varies slightly near the bottom
     * of the listview in order to account for the fact that the bottom bounds of the actual
     * listview cannot be modified.
     */
    private int[] getTopAndBottomTranslations(int top, int bottom, int yDelta,
                                              boolean isExpanding) {
        int yTranslateTop = 0;
        int yTranslateBottom = yDelta;

        int height = bottom - top;

        if (isExpanding) {
            boolean isOverTop = top < 0;
            boolean isBelowBottom = (top + height + yDelta) > getHeight();
            if (isOverTop) {
                yTranslateTop = top;
                yTranslateBottom = yDelta - yTranslateTop;
            } else if (isBelowBottom) {
                int deltaBelow = top + height + yDelta - getHeight();
                yTranslateTop = top - deltaBelow < 0 ? top : deltaBelow;
                yTranslateBottom = yDelta - yTranslateTop;
            }
        } else {
            int offset = computeVerticalScrollOffset();
            int range = computeVerticalScrollRange();
            int extent = computeVerticalScrollExtent();
            int leftoverExtent = range - offset - extent;

            boolean isCollapsingBelowBottom = (yTranslateBottom > leftoverExtent);
            boolean isCellCompletelyDisappearing = bottom - yTranslateBottom < 0;

            if (isCollapsingBelowBottom) {
                yTranslateTop = yTranslateBottom - leftoverExtent;
                yTranslateBottom = yDelta - yTranslateTop;
            } else if (isCellCompletelyDisappearing) {
                yTranslateBottom = bottom;
                yTranslateTop = yDelta - yTranslateBottom;
            }
        }

        return new int[]{yTranslateTop, yTranslateBottom};
    }

    /**
     * This method expands the view that was clicked and animates all the views
     * around it to make room for the expanding view. There are several steps required
     * to do this which are outlined below.
     * <p/>
     * 1. Store the current top and bottom bounds of each visible item in the listview.
     * 2. Update the layout parameters of the selected view. In the context of this
     * method, the view should be originally collapsed and set to some custom height.
     * The layout parameters are updated so as to wrap the content of the additional
     * text that is to be displayed.
     * <p/>
     * After invoking a layout to take place, the listview will order all the items
     * such that there is space for each view. This layout will be independent of what
     * the bounds of the items were prior to the layout so two pre-draw passes will
     * be made. This is necessary because after the layout takes place, some views that
     * were visible before the layout may now be off bounds but a reference to these
     * views is required so the animation completes as intended.
     * <p/>
     * 3. The first predraw pass will set the bounds of all the visible items to
     * their original location before the layout took place and then force another
     * layout. Since the bounds of the cells cannot be set directly, the method
     * setSelectionFromTop can be used to achieve a very similar effect.
     * 4. The expanding view's bounds are animated to what the final values should be
     * from the original bounds.
     * 5. The bounds above the expanding view are animated upwards while the bounds
     * below the expanding view are animated downwards.
     * 6. The extra text is faded in as its contents become visible throughout the
     * animation process.
     * <p/>
     * It is important to note that the listview is disabled during the animation
     * because the scrolling behaviour is unpredictable if the bounds of the items
     * within the listview are not constant during the scroll.
     */

    private void expandView(final ExpandingListItemView expandingListItemView) {
        final ExpandableListItem expandingListItem = (ExpandableListItem) getItemAtPosition(getPositionForView
                (expandingListItemView));

        /* Store the original top and bottom bounds of all the cells.*/
        final int oldExpandingViewTop = expandingListItemView.getTop();
        final int oldExpandingViewBottom = expandingListItemView.getBottom();

        final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.setHasTransientState(true);
            oldCoordinates.put(v, new int[]{v.getTop(), v.getBottom()});
        }

        /* Update the layout so the extra content becomes visible.*/
        final View expandingLayout = expandingListItemView.getExpandingLayout();
        expandingLayout.setVisibility(View.VISIBLE);

        /* Add an onPreDraw Listener to the listview. onPreDraw will get invoked after onLayout
        * and onMeasure have run but before anything has been drawn. This
        * means that the final post layout properties for all the items have already been
        * determined, but still have not been rendered onto the screen.*/
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                /* Determine if this is the first or second pass.*/
                if (!mShouldRemoveObserver) {
                    mShouldRemoveObserver = true;

                    /* Calculate what the parameters should be for setSelectionFromTop.
                    * The ListView must be offset in a way, such that after the animation
                    * takes place, all the cells that remain visible are rendered completely
                    * by the ListView.*/
                    int newExpandingViewTop = expandingListItemView.getTop();
                    int newExpandingViewBottom = expandingListItemView.getBottom();

                    int newExpandingViewHeight = newExpandingViewBottom - newExpandingViewTop;
                    int oldExpandingViewHeight = oldExpandingViewBottom - oldExpandingViewTop;
                    int deltaExpandingViewHeight = newExpandingViewHeight - oldExpandingViewHeight;

                    mExpandingTranslate = getTopAndBottomTranslations(oldExpandingViewTop, oldExpandingViewBottom, deltaExpandingViewHeight, true);

                    int currentExpandingViewTop = expandingListItemView.getTop();
                    int futureExpandingViewTop = oldExpandingViewTop - mExpandingTranslate[0];

                    int firstChildStartTop = getChildAt(0).getTop();
                    int firstVisiblePosition = getFirstVisiblePosition();
                    int deltaExpandingViewTop = currentExpandingViewTop - futureExpandingViewTop;

                    int i;
                    int childCount = getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View v = getChildAt(i);
                        int height = v.getBottom() - Math.max(0, v.getTop());
                        if (deltaExpandingViewTop - height > 0) {
                            firstVisiblePosition++;
                            deltaExpandingViewTop -= height;
                        } else {
                            break;
                        }
                    }

                    if (i > 0) {
                        firstChildStartTop = 0;
                    }

                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaExpandingViewTop);

                    /* Request another layout to update the layout parameters of the cells.*/
                    requestLayout();

                    /* Return false such that the ListView does not redraw its contents on
                     * this layout but only updates all the parameters associated with its
                     * children.*/
                    return false;
                }

                /* Remove the predraw listener so this method does not keep getting called. */
                mShouldRemoveObserver = false;
                observer.removeOnPreDrawListener(this);

                int yExpandingViewTranslateTop = mExpandingTranslate[0];
                int yExpandingViewTranslateBottom = mExpandingTranslate[1];

                ArrayList<Animator> animations = new ArrayList<Animator>();

                int index = indexOfChild(expandingListItemView);

                /* Loop through all the views that were on the screen before the cell was
                *  expanded. Some cells will still be children of the ListView while
                *  others will not. The cells that remain children of the ListView
                *  simply have their bounds animated appropriately. The cells that are no
                *  longer children of the ListView also have their bounds animated, but
                *  must also be added to a list of views which will be drawn in dispatchDraw.*/
                for (View v : oldCoordinates.keySet()) {
                    int[] old = oldCoordinates.get(v);
                    v.setTop(old[0]);
                    v.setBottom(old[1]);
                    if (v.getParent() == null) {
                        mViewsToDraw.add(v);
                        int delta = old[0] < oldExpandingViewTop ? -yExpandingViewTranslateTop : yExpandingViewTranslateBottom;
                        animations.add(getAnimation(v, delta, delta));
                    } else {
                        int i = indexOfChild(v);
                        if (v != expandingListItemView) {
                            int delta = i > index ? yExpandingViewTranslateBottom : -yExpandingViewTranslateTop;
                            animations.add(getAnimation(v, delta, delta));
                        }
                        v.setHasTransientState(false);
                    }
                }

                /* Adds animation for expanding the cell that was clicked. */
                animations.add(getAnimation(expandingListItemView, -yExpandingViewTranslateTop, yExpandingViewTranslateBottom));

                /* Adds an animation for fading in the extra content. */
                animations.add(ObjectAnimator.ofFloat(expandingListItemView.getExpandingLayout(),
                        View.ALPHA, 0, 1));

                /* Disabled the ListView for the duration of the animation.*/
                setEnabled(false);
                setClickable(false);

                /* Play all the animations created above together at the same time. */
                AnimatorSet s = new AnimatorSet();
                s.playTogether(animations);
                s.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        expandingListItem.setExpanded(true);
                        setEnabled(true);
                        setClickable(true);
                        if (mViewsToDraw.size() > 0) {
                            for (View v : mViewsToDraw) {
                                v.setHasTransientState(false);
                            }
                        }
                        mViewsToDraw.clear();
                        mCurrentExpandedListItemView = expandingListItemView;
                    }
                });
                s.start();
                return true;
            }
        });
    }

    /**
     * By overriding dispatchDraw, we can draw the cells that disappear during the
     * expansion process. When the cell expands, some items below or above the expanding
     * cell may be moved off screen and are thus no longer children of the ListView's
     * layout. By storing a reference to these views prior to the layout, and
     * guaranteeing that these cells do not get recycled, the cells can be drawn
     * directly onto the canvas during the animation process. After the animation
     * completes, the references to the extra views can then be discarded.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mViewsToDraw.size() == 0) {
            return;
        }

        for (View v : mViewsToDraw) {
            canvas.translate(0, v.getTop());
            v.draw(canvas);
            canvas.translate(0, -v.getTop());
        }
    }

    /**
     * This method collapses the view that was clicked and animates all the views
     * around it to close around the collapsing view. There are several steps required
     * to do this which are outlined below.
     * <p/>
     * 1. Update the layout parameters of the view clicked so as to minimize its height
     * to the original collapsed (default) state.
     * 2. After invoking a layout, the listview will shift all the cells so as to display
     * them most efficiently. Therefore, during the first predraw pass, the listview
     * must be offset by some amount such that given the custom bound change upon
     * collapse, all the cells that need to be on the screen after the layout
     * are rendered by the listview.
     * 3. On the second predraw pass, all the items are first returned to their original
     * location (before the first layout).
     * 4. The collapsing view's bounds are animated to what the final values should be.
     * 5. The bounds above the collapsing view are animated downwards while the bounds
     * below the collapsing view are animated upwards.
     * 6. The extra text is faded out as its contents become visible throughout the
     * animation process.
     */

    private void collapseView(final ExpandingListItemView collapsingListItemView) {
        final ExpandableListItem collapsingListItem = (ExpandableListItem) getItemAtPosition
                (getPositionForView(collapsingListItemView));

        /* Store the original top and bottom bounds of all the cells.*/
        final int oldCollapsingViewTop = collapsingListItemView.getTop();
        final int oldCollapsingViewBottom = collapsingListItemView.getBottom();

        final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.setHasTransientState(true);
            oldCoordinates.put(v, new int[]{v.getTop(), v.getBottom()});
        }

        /* Update the layout so the extra content becomes invisible.*/
        collapsingListItemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                collapsingListItem.getCollapsedHeight()));

         /* Add an onPreDraw listener. */
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                if (!mShouldRemoveObserver) {
                    /*Same as for expandingView, the parameters for setSelectionFromTop must
                    * be determined such that the necessary cells of the ListView are rendered
                    * and added to it.*/
                    mShouldRemoveObserver = true;

                    int newCollapsingViewTop = collapsingListItemView.getTop();
                    int newCollapsingViewBottom = collapsingListItemView.getBottom();

                    int newCollapsingViewHeight = newCollapsingViewBottom - newCollapsingViewTop;
                    int oldCollapsingViewHeight = oldCollapsingViewBottom - oldCollapsingViewTop;
                    int deltaCollapsingViewHeight = oldCollapsingViewHeight - newCollapsingViewHeight;

                    mCollapsingTranslate = getTopAndBottomTranslations(oldCollapsingViewTop, oldCollapsingViewBottom, deltaCollapsingViewHeight, false);

                    int currentCollapsingViewTop = collapsingListItemView.getTop();
                    int futureCollapsingViewTop = oldCollapsingViewTop + mCollapsingTranslate[0];

                    int firstChildStartTop = getChildAt(0).getTop();
                    int firstVisiblePosition = getFirstVisiblePosition();
                    int deltaCollapsingViewTop = currentCollapsingViewTop - futureCollapsingViewTop;

                    int i;
                    int childCount = getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View v = getChildAt(i);
                        int height = v.getBottom() - Math.max(0, v.getTop());
                        if (deltaCollapsingViewTop - height > 0) {
                            firstVisiblePosition++;
                            deltaCollapsingViewTop -= height;
                        } else {
                            break;
                        }
                    }

                    if (i > 0) {
                        firstChildStartTop = 0;
                    }

                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaCollapsingViewTop);

                    requestLayout();

                    return false;
                }

                mShouldRemoveObserver = false;
                observer.removeOnPreDrawListener(this);

                int yCollapsingViewTranslateTop = mCollapsingTranslate[0];
                int yCollapsingViewTranslateBottom = mCollapsingTranslate[1];

                int collapsingIndex = indexOfChild(collapsingListItemView);
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    int[] old = oldCoordinates.get(v);
                    if (old != null) {
                        /* If the cell was present in the ListView before the collapse and
                        * after the collapse then the bounds are reset to their old values.*/
                        v.setTop(old[0]);
                        v.setBottom(old[1]);
                        v.setHasTransientState(false);
                    } else {
                        /* If the cell is present in the ListView after the collapse but
                         * not before the collapse then the bounds are calculated using
                         * the bottom and top translation of the collapsing cell.*/
                        int delta = i > collapsingIndex ? yCollapsingViewTranslateBottom : -yCollapsingViewTranslateTop;
                        v.setTop(v.getTop() + delta);
                        v.setBottom(v.getBottom() + delta);
                    }
                }

                /* Animates all the cells present on the screen after the collapse. */
                ArrayList<Animator> animations = new ArrayList<Animator>();
                for (int i = 0; i < childCount; i++) {
                    View v = getChildAt(i);
                    if (v != collapsingListItemView) {
                        float diff = i > collapsingIndex ? -yCollapsingViewTranslateBottom : yCollapsingViewTranslateTop;
                        animations.add(getAnimation(v, diff, diff));
                    }
                }

                /* Adds animation for collapsing the cell that was clicked. */
                animations.add(getAnimation(collapsingListItemView, yCollapsingViewTranslateTop, -yCollapsingViewTranslateBottom));

                /* Adds an animation for fading out the extra content. */
                final View collapsingLayout = collapsingListItemView.getExpandingLayout();
                animations.add(ObjectAnimator.ofFloat(collapsingLayout, View.ALPHA, 1, 0));

                /* Disabled the ListView for the duration of the animation.*/
                setEnabled(false);
                setClickable(false);

                /* Play all the animations created above together at the same time. */
                AnimatorSet s = new AnimatorSet();
                s.playTogether(animations);
                s.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        collapsingLayout.setVisibility(View.GONE);
                        collapsingListItemView.setLayoutParams(new LayoutParams(
                                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        collapsingListItem.setExpanded(false);
                        setEnabled(true);
                        setClickable(true);
                        /* Note that alpha must be set back to 1 in case this view is reused
                        * by a cell that was expanded, but not yet collapsed, so its state
                        * should persist in an expanded state with the extra content visible.*/
                        collapsingLayout.setAlpha(1);
                    }
                });
                s.start();

                return true;
            }
        });
    }

    /**
     * This is a mess. So many things going on, that are very difficult to track. Abandoning for now.
     * -Andrew
     * @param collapsingListItemView
     * @param expandingListItemView
     */
    private void collapseOldViewExpandNew(final ExpandingListItemView collapsingListItemView, final ExpandingListItemView expandingListItemView) {
        final ExpandableListItem collapsingListItem = (ExpandableListItem) getItemAtPosition
                (getPositionForView(collapsingListItemView));
        final ExpandableListItem expandingListItem = (ExpandableListItem) getItemAtPosition(getPositionForView
                (expandingListItemView));

        final int oldCollapsingViewTop = collapsingListItemView.getTop();
        final int oldCollapsingViewBottom = collapsingListItemView.getBottom();
        final int oldExpandingViewTop = expandingListItemView.getTop();
        final int oldExpandingViewBottom = expandingListItemView.getBottom();

        final HashMap<View, int[]> oldCoordinates = new HashMap<View, int[]>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.setHasTransientState(true);
            oldCoordinates.put(v, new int[]{v.getTop(), v.getBottom()});
        }

        /* Update the layout so the extra content becomes invisible.*/
        collapsingListItemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                collapsingListItem.getCollapsedHeight()));

        /* Update the layout so the extra content becomes visible.*/
        final View expandingLayout = expandingListItemView.getExpandingLayout();
        expandingLayout.setVisibility(View.VISIBLE);

         /* Add an onPreDraw listener. */
        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                /* Determine if this is the first or second pass.*/
                if (!mShouldRemoveObserver) {
                    /*Same as for expandingView, the parameters for setSelectionFromTop must
                    * be determined such that the necessary cells of the ListView are rendered
                    * and added to it.*/
                    mShouldRemoveObserver = true;

                    /* Calculate what the parameters should be for setSelectionFromTop.
                    * The ListView must be offset in a way, such that after the animation
                    * takes place, all the cells that remain visible are rendered completely
                    * by the ListView.*/

                    int newExpandingViewTop = expandingListItemView.getTop();
                    int newExpandingViewBottom = expandingListItemView.getBottom();
                    int newExpandingViewHeight = newExpandingViewBottom - newExpandingViewTop;
                    int oldExpandingViewHeight = oldExpandingViewBottom - oldExpandingViewTop;
                    int deltaExpandingViewHeight = newExpandingViewHeight - oldExpandingViewHeight;
                    mExpandingTranslate = getTopAndBottomTranslations(oldExpandingViewTop, oldExpandingViewBottom, deltaExpandingViewHeight, true);

                    int newCollapsingViewTop = collapsingListItemView.getTop();
                    int newCollapsingViewBottom = collapsingListItemView.getBottom();
                    int newCollapsingViewHeight = newCollapsingViewBottom - newCollapsingViewTop;
                    int oldCollapsingViewHeight = oldCollapsingViewBottom - oldCollapsingViewTop;
                    int deltaCollapsingViewHeight = oldCollapsingViewHeight - newCollapsingViewHeight;
                    // TODO
                    mCollapsingTranslate = getTopAndBottomTranslations(oldCollapsingViewTop, oldCollapsingViewBottom, deltaCollapsingViewHeight, false);

//                    int currentCollapsingViewTop = collapsingListItemView.getTop();
//                    int futureCollapsingViewTop = oldCollapsingViewTop + mCollapsingTranslate[0];

                    int currentExpandingViewTop = expandingListItemView.getTop();
                    int futureExpandingViewTop = oldExpandingViewTop - mExpandingTranslate[0];

                    int firstChildStartTop = getChildAt(0).getTop();
                    int firstVisiblePosition = getFirstVisiblePosition();

                    int deltaExpandingViewTop = currentExpandingViewTop - futureExpandingViewTop;
//                    int deltaCollapsingViewTop = currentCollapsingViewTop - futureCollapsingViewTop;

                    int i;
                    int childCount = getChildCount();
                    for (i = 0; i < childCount; i++) {
                        View v = getChildAt(i);
                        int height = v.getBottom() - Math.max(0, v.getTop());
                        // TODO figure this part out
//                        if (deltaCollapsingViewTop - height > 0) {
//                            firstVisiblePosition++;
//                            deltaCollapsingViewTop -= height;
                        if (deltaExpandingViewTop - height > 0) {
                            firstVisiblePosition++;
                            deltaExpandingViewTop -= height;
                        } else {
                            break;
                        }
                    }

                    if (i > 0) {
                        firstChildStartTop = 0;
                    }

//                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaCollapsingViewTop);
                    setSelectionFromTop(firstVisiblePosition, firstChildStartTop - deltaExpandingViewTop);

                    requestLayout();

                    return false;
                }

                /* Remove the predraw listener so this method does not keep getting called. */
                mShouldRemoveObserver = false;
                observer.removeOnPreDrawListener(this);

                int yCollapsingViewTranslateTop = mCollapsingTranslate[0];
                int yCollapsingViewTranslateBottom = mCollapsingTranslate[1];

                int yExpandingViewTranslateTop = mExpandingTranslate[0];
                int yExpandingViewTranslateBottom = mExpandingTranslate[1];

                ArrayList<Animator> animations = new ArrayList<Animator>();

                int index = indexOfChild(expandingListItemView);

                /* Loop through all the views that were on the screen before the cell was
                *  expanded. Some cells will still be children of the ListView while
                *  others will not. The cells that remain children of the ListView
                *  simply have their bounds animated appropriately. The cells that are no
                *  longer children of the ListView also have their bounds animated, but
                *  must also be added to a list of views which will be drawn in dispatchDraw.*/
                for (View v : oldCoordinates.keySet()) {
                    int[] old = oldCoordinates.get(v);
                    v.setTop(old[0]);
                    v.setBottom(old[1]);
                    if (v.getParent() == null) {
                        mViewsToDraw.add(v);
                        int delta = old[0] < oldExpandingViewTop ? -yExpandingViewTranslateTop : yExpandingViewTranslateBottom;
                        animations.add(getAnimation(v, delta, delta));
                    } else {
                        int i = indexOfChild(v);
                        boolean belowExpandingView = i > index;
                        int delta = belowExpandingView ? yExpandingViewTranslateBottom : -yExpandingViewTranslateTop;
                        if (v == collapsingListItemView) {
                            int newCollapsingViewTop = collapsingListItemView.getTop();
                            int newCollapsingViewBottom = collapsingListItemView.getBottom();
//                            int deltaSize = oldCollapsingViewBottom - newCollapsingViewBottom;
                            int deltaTop = belowExpandingView ? yExpandingViewTranslateBottom : -yExpandingViewTranslateTop;
                            // TODO if newBottom > oldBottom ?
                            int deltaBottom = belowExpandingView ? oldCollapsingViewBottom - newCollapsingViewBottom : oldCollapsingViewBottom - newCollapsingViewBottom;
                            animations.add(getAnimation(v, deltaTop, deltaBottom));

                            int newExpandingViewTop = expandingListItemView.getTop();
                            int newExpandingViewBottom = expandingListItemView.getBottom();
                            boolean expandingBelowCollapsing = index > i;
                            deltaTop = expandingBelowCollapsing ? oldExpandingViewTop - newExpandingViewTop : -yExpandingViewTranslateTop;
                            deltaBottom = expandingBelowCollapsing ? newExpandingViewBottom - oldExpandingViewBottom : newExpandingViewBottom - oldExpandingViewBottom;
                            animations.add(getAnimation(expandingListItemView, deltaTop, deltaBottom));
                        } else if (v != expandingListItemView) {
                            animations.add(getAnimation(v, delta, delta));
                        }
                        v.setHasTransientState(false);
                    }
                }

                /* Adds animation for expanding the cell that was clicked. */
//                animations.add(getAnimation(collapsingListItemView, yCollapsingViewTranslateTop, -yCollapsingViewTranslateBottom));
//                animations.add(getAnimation(expandingListItemView, -yExpandingViewTranslateTop, yExpandingViewTranslateBottom));

                final View expandingLayout = expandingListItemView.getExpandingLayout();
                final View collapsingLayout = collapsingListItemView.getExpandingLayout();
                /* Adds an animation for fading in the extra content. */
                animations.add(ObjectAnimator.ofFloat(expandingLayout, View.ALPHA, 0, 1));
                animations.add(ObjectAnimator.ofFloat(collapsingLayout, View.ALPHA, 1, 0));

                /* Disabled the ListView for the duration of the animation.*/
                setEnabled(false);
                setClickable(false);

                /* Play all the animations created above together at the same time. */
                AnimatorSet s = new AnimatorSet();
                s.playTogether(animations);
                s.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        collapsingLayout.setVisibility(View.GONE);
                        collapsingListItem.setExpanded(false);
                        collapsingListItemView.setLayoutParams(new LayoutParams(
                                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        /* Note that alpha must be set back to 1 in case this view is reused
                        * by a cell that was expanded, but not yet collapsed, so its state
                        * should persist in an expanded state with the extra content visible.*/
                        collapsingLayout.setAlpha(1);

                        expandingListItem.setExpanded(true);
                        setEnabled(true);
                        setClickable(true);
                        if (mViewsToDraw.size() > 0) {
                            for (View v : mViewsToDraw) {
                                v.setHasTransientState(false);
                            }
                        }
                        mViewsToDraw.clear();

                        mCurrentExpandedListItemView = expandingListItemView;
                    }
                });
                s.start();
                return true;
            }
        });
    }

    /**
     * This method takes some view and the values by which its top and bottom bounds
     * should be changed by. Given these params, an animation which will animate
     * these bound changes is created and returned.
     */

    private Animator getAnimation(final View view, float translateTop, float translateBottom) {

        int top = view.getTop();
        int bottom = view.getBottom();

        int endTop = (int) (top + translateTop);
        int endBottom = (int) (bottom + translateBottom);

        PropertyValuesHolder translationTop = PropertyValuesHolder.ofInt("top", top, endTop);
        PropertyValuesHolder translationBottom = PropertyValuesHolder.ofInt("bottom", bottom,
                endBottom);

        return ObjectAnimator.ofPropertyValuesHolder(view, translationTop, translationBottom);
    }
}
