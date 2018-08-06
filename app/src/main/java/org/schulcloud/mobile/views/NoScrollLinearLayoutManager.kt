package org.schulcloud.mobile.views

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NoScrollLinearLayoutManager(
    context: Context,
    @RecyclerView.Orientation orientation: Int = VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun canScrollVertically() = false
    override fun isAutoMeasureEnabled() = true
}
