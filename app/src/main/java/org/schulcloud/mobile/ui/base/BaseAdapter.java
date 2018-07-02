package org.schulcloud.mobile.ui.base;

import android.support.v7.widget.RecyclerView;

import org.schulcloud.mobile.ui.main.MainActivity;

/**
 * Date: 2/19/2018
 */
public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    private MainActivity mMainActivity;
    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        if (!(mRecyclerView.getContext() instanceof MainActivity))
            throw new IllegalStateException("BaseAdapter may only be used in MainActivity");
        mMainActivity = (MainActivity) mRecyclerView.getContext();
    }
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mRecyclerView = null;
        mMainActivity = null;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
    public MainActivity getMainActivity() {
        return mMainActivity;
    }
}
