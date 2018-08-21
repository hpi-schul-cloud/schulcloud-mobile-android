package org.schulcloud.mobile.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import org.schulcloud.mobile.R

/**
 * Similar to [androidx.recyclerview.widget.DividerItemDecoration], but with configurable divider locations (before first
 * and after last item) and insets.
 */
class DividerItemDecoration(
    context: Context,
    @LinearLayoutCompat.DividerMode val showDividers: Int = BEGINNING or MIDDLE or END,
    val inset: Int? = null,
    val insetStart: Int? = null,
    @RecyclerView.Orientation val orientation: Int = VERTICAL
) : RecyclerView.ItemDecoration() {
    companion object {
        private val TAG = DividerItemDecoration::class.java.simpleName

        const val NONE = LinearLayout.SHOW_DIVIDER_NONE
        const val BEGINNING = LinearLayout.SHOW_DIVIDER_BEGINNING
        const val MIDDLE = LinearLayout.SHOW_DIVIDER_MIDDLE
        const val END = LinearLayout.SHOW_DIVIDER_END

        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        fun middle(
            context: Context,
            @LinearLayoutCompat.DividerMode showDividers: Int = BEGINNING or MIDDLE or END,
            @RecyclerView.Orientation orientation: Int = VERTICAL
        ): DividerItemDecoration {
            return DividerItemDecoration(context,
                    showDividers,
                    inset = context.resources.getDimensionPixelOffset(R.dimen.material_dividerMiddle_inset),
                    orientation = orientation)
        }
    }

    private var divider = ResourcesCompat.getDrawable(context.resources, R.drawable.divider_dark, context.theme)!!
    private val bounds = Rect()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null)
            return

        if (orientation == VERTICAL)
            drawVertical(c, parent)
        else
            drawHorizontal(c, parent)
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        if (parent.clipToPadding)
            canvas.clipRect(parent.paddingLeft,
                    parent.paddingTop,
                    parent.width - parent.paddingRight,
                    parent.height - parent.paddingBottom)

        fun drawDividerHorizontal(canvas: Canvas, top: Int, bottom: Int = top + divider.intrinsicHeight) {
            val insetStart = insetStart ?: inset ?: 0
            val insetEnd = inset ?: 0
            drawDivider(canvas, bounds.left + insetStart, top,
                    bounds.right - insetEnd, bottom)
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (!hasDividersBefore(parent, i))
                continue

            val child = parent[i]
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val top = bounds.top + Math.round(child.translationY)
            drawDividerHorizontal(canvas, top)
        }

        if (hasDividersBefore(parent, childCount)) {
            val bottom = if (parent.isEmpty())
                parent.height - if (parent.clipToPadding) parent.paddingBottom else 0
            else {
                val child = parent[childCount - 1]
                parent.getDecoratedBoundsWithMargins(child, bounds)
                bounds.bottom + Math.round(child.translationY)
            }
            drawDividerHorizontal(canvas, bottom - 2 * divider.intrinsicHeight, bottom)
        }

        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        if (parent.clipToPadding)
            canvas.clipRect(parent.paddingLeft,
                    parent.paddingTop,
                    parent.width - parent.paddingRight,
                    parent.height - parent.paddingBottom)

        fun drawDividerVertical(canvas: Canvas, left: Int, right: Int = left + divider.intrinsicWidth) {
            val insetStart = insetStart ?: inset ?: 0
            val insetEnd = inset ?: 0
            drawDivider(canvas, left, bounds.top + insetStart,
                    right, bounds.bottom - insetEnd)
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (!hasDividersBefore(parent, i))
                continue

            val child = parent.getChildAt(i)
            parent.layoutManager?.getDecoratedBoundsWithMargins(child, bounds)
            val left = bounds.right + Math.round(child.translationX)
            drawDividerVertical(canvas, left)
        }

        if (hasDividersBefore(parent, childCount)) {
            val right = if (parent.isEmpty())
                parent.width - if (parent.clipToPadding) parent.paddingRight else 0
            else {
                val child = parent[childCount - 1]
                parent.getDecoratedBoundsWithMargins(child, bounds)
                bounds.right + Math.round(child.translationY)
            }
            drawDividerVertical(canvas, right - 2 * divider.intrinsicWidth, right)
        }

        canvas.restore()
    }

    private fun hasDividersBefore(parent: RecyclerView, index: Int) = when (index) {
        0 -> (showDividers and BEGINNING) != 0
        parent.childCount -> (showDividers and END) != 0
        else -> (showDividers and MIDDLE) != 0
    }

    private fun drawDivider(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        divider.setBounds(left, top, right, bottom)
        divider.draw(canvas)
    }
}
