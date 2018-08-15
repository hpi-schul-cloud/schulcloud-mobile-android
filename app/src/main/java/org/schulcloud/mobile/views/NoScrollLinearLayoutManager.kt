package org.schulcloud.mobile.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class NoScrollLinearLayoutManager(context: Context,
                                  @RecyclerView.Orientation orientation: Int = VERTICAL,
                                  reverseLayout: Boolean = false)
    : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun canScrollVertically(): Boolean {
        return false
    }

}
