package org.schulcloud.mobile.ui.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private T mItem;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    @NonNull
    public Context getContext() {
        return itemView.getContext();
    }

    public void setItem(@NonNull T item) {
        mItem = item;
        onItemSet(item);
    }
    protected abstract void onItemSet(@NonNull T item);
    @NonNull
    public final T getItem() {
        return mItem;
    }
}
