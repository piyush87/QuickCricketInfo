package com.panduka.quickcricketinfo.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by pandukadesilva on 3/5/16.
 */
public class ListRefreshViewLayout extends SwipeRefreshLayout {
    RecyclerView matchList;
    public ListRefreshViewLayout(Context context, RecyclerView matchList) {
        super(context);
        this.matchList = matchList;
    }


    /**
     * As mentioned above, we need to override this method to properly signal when a
     * 'swipe-to-refresh' is possible.
     *
     * @return true if the {@link android.widget.ListView} is visible and can scroll up.
     */
    @Override
    public boolean canChildScrollUp() {
        if (matchList.getVisibility() == View.VISIBLE) {
            return canListViewScrollUp(matchList);
        } else {
            return false;
        }
    }

    private static boolean canListViewScrollUp(RecyclerView listView) {
        return ViewCompat.canScrollVertically(listView, -1);

    }
}
