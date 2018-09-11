package org.schulcloud.mobile.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.DrawableCompat
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.isLtr
import kotlin.properties.Delegates

open class CompatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = CompatTextView::class.java.simpleName
    }

    private var isInitialized = false
    private fun <T> updateDrawablesObservable(initialValue: T) = Delegates.observable(initialValue) { _, _, _ ->
        // Avoids many calls when initializing
        if (isInitialized)
            updateDrawables()
    }

    var drawableStart by updateDrawablesObservable<Drawable?>(null)
    var drawableEnd by updateDrawablesObservable<Drawable?>(null)
    var drawableBottom by updateDrawablesObservable<Drawable?>(null)
    var drawableTop by updateDrawablesObservable<Drawable?>(null)

    var drawableStartVisible by updateDrawablesObservable(true)
    var drawableEndVisible by updateDrawablesObservable(true)
    var drawableTopVisible by updateDrawablesObservable(true)
    var drawableBottomVisible by updateDrawablesObservable(true)

    @delegate:ColorInt
    var drawableTintColor by updateDrawablesObservable<Int?>(null)

    init {
        context.withStyledAttributes(attrs, R.styleable.CompatTextView) {
            // To use vector drawables on pre-21
            // https://stackoverflow.com/a/40250753/6220609
            fun drawable(@StyleableRes attrId: Int): Drawable? {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    getDrawable(attrId)
                else
                    getResourceId(attrId, 0)
                            .takeIf { it != 0 }
                            ?.let { AppCompatResources.getDrawable(context, id) }
            }

            drawableStart = drawable(R.styleable.CompatTextView_drawableStart)
            drawableEnd = drawable(R.styleable.CompatTextView_drawableEnd)
            drawableBottom = drawable(R.styleable.CompatTextView_drawableBottom)
            drawableTop = drawable(R.styleable.CompatTextView_drawableTop)

            drawableStartVisible = getBoolean(R.styleable.CompatTextView_drawableStartVisible, true)
            drawableEndVisible = getBoolean(R.styleable.CompatTextView_drawableEndVisible, true)
            drawableTopVisible = getBoolean(R.styleable.CompatTextView_drawableTopVisible, true)
            drawableBottomVisible = getBoolean(R.styleable.CompatTextView_drawableBottomVisible, true)

            if (hasValue(R.styleable.CompatTextView_drawableTint))
                drawableTintColor = getColorOrThrow(R.styleable.CompatTextView_drawableTint)

            isInitialized = true
            updateDrawables()
        }
    }

    private fun updateDrawables() {
        fun drawableIfVisible(drawable: Drawable?, visible: Boolean): Drawable? {
            return if (drawable != null && visible)
                drawableTintColor?.let {
                    DrawableCompat.wrap(drawable).apply {
                        mutate()
                        DrawableCompat.setTint(this, it)
                    }
                } ?: drawable
            else null
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ->
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                        drawableIfVisible(drawableStart, drawableStartVisible),
                        drawableIfVisible(drawableTop, drawableTopVisible),
                        drawableIfVisible(drawableEnd, drawableEndVisible),
                        drawableIfVisible(drawableBottom, drawableBottomVisible))

            isLtr() -> setCompoundDrawablesWithIntrinsicBounds(
                    drawableIfVisible(drawableStart, drawableStartVisible),
                    drawableIfVisible(drawableTop, drawableTopVisible),
                    drawableIfVisible(drawableEnd, drawableEndVisible),
                    drawableIfVisible(drawableBottom, drawableBottomVisible))

            else -> setCompoundDrawablesWithIntrinsicBounds(
                    drawableIfVisible(drawableEnd, drawableEndVisible),
                    drawableIfVisible(drawableTop, drawableTopVisible),
                    drawableIfVisible(drawableStart, drawableStartVisible),
                    drawableIfVisible(drawableBottom, drawableBottomVisible))
        }
    }
}
