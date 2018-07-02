package org.schulcloud.mobile.ui.courses.topic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private T mItem;

    BaseViewHolder(View itemView) {
        super(itemView);
    }

    @NonNull
    public Context getContext() {
        return itemView.getContext();
    }

    void setItem(@NonNull T item) {
        mItem = item;
        //boolean hidden = item.hidden != null ? item.hidden : false;
        //ViewUtil.setVisibility(itemView, !hidden);
        onItemSet(item);
    }
    abstract void onItemSet(@NonNull T item);
    @NonNull
    final T getItem() {
        return mItem;
    }
}
