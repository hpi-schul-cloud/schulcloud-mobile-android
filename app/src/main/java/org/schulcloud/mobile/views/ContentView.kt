package org.schulcloud.mobile.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import kotlinx.android.synthetic.main.view_content.view.*
import org.schulcloud.mobile.R


open class ContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = ContentView::class.java.simpleName
    }

    private var isInitialized: Boolean = false
    private var hasToolbar: Boolean = false

    init {
        orientation = VERTICAL
        View.inflate(context, R.layout.view_content, this)
        isInitialized = true
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (!isInitialized)
            super.addView(child, index, params)
        else if (child?.id == R.id.toolbar || child?.id == R.id.toolbarWrapper) {
            hasToolbar = true
            super.addView(child, 0, params)
        } else {
            if (!hasToolbar) {
                hasToolbar = true
                val toolbar = LayoutInflater.from(context).inflate(R.layout.toolbar, this, false)
                super.addView(toolbar, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            }

            swipeRefresh.addView(child, index, params)
        }
    }
}
