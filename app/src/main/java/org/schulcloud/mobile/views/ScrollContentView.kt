package org.schulcloud.mobile.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import kotlinx.android.synthetic.main.view_content_scroll.view.*
import org.schulcloud.mobile.R

open class ScrollContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = ScrollContentView::class.java.simpleName
    }

    private var initialized: Boolean = false

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.view_content_scroll, this)
        initialized = true
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (!initialized)
            super.addView(child, index, params)
        else
            swipeRefresh.addView(child, index, params)
    }
}
