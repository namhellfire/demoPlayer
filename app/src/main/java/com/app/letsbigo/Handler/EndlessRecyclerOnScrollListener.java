package com.app.letsbigo.Handler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by nguyennam on 3/11/17.
 */

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public final static String TAG = "EndlessRecyclerOnScrollListener";
    private GridLayoutManager gridLayoutManager;
    int mLastFirstVisibleItem = 0;

    public EndlessRecyclerOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final int currentFirstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
        final int currentLastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
        final int totalItem = gridLayoutManager.getItemCount();


    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }


}
