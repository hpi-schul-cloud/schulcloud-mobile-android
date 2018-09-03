package org.schulcloud.mobile.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

/**
 * Similar to [DividerItemDecoration], but no divider is drawn after the last item.
 */
class MiddleDividerItemDecoration(
    context: Context,
    @RecyclerView.Orientation val orientation: Int = VERTICAL
) : RecyclerView.ItemDecoration() {
    companion object {
        private val TAG = MiddleDividerItemDecoration::class.java.simpleName

        @RecyclerView.Orientation
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        @RecyclerView.Orientation
        const val VERTICAL = RecyclerView.VERTICAL
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    private var divider: Drawable
    private val mBounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        val div = a.getDrawable(0)
        if (div == null)
            Log.w(TAG, "@android:attr/listDivider was not set in the theme used for this MiddleDividerItemDecoration.")
        divider = div
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null)
            return

        if (orientation == VERTICAL)
            drawVertical(c, parent)
        else
            drawHorizontal(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + Math.round(child.translationY)
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(parent.paddingLeft, top,
                    parent.width - parent.paddingRight, bottom)
        } else {
            top = 0
            bottom = parent.height
        }

        for (i in 0 until (parent.childCount - 1)) {
            val child = parent.getChildAt(i)
            parent.layoutManager?.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + Math.round(child.translationX)
            val left = right - divider.intrinsicWidth
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }
}
