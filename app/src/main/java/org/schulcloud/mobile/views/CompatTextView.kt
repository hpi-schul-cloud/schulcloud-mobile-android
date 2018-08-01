package org.schulcloud.mobile.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.AttrRes
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.isLtr

open class CompatTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        val TAG: String = CompatTextView::class.java.simpleName
    }

    init {
        initAttrs(attrs)
    }

    @Suppress("ComplexMethod")
    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null)
            return

        val attributeArray = context.obtainStyledAttributes(attrs, R.styleable.CompatTextView)

        // To use vector drawables on pre-21
        // https://stackoverflow.com/a/40250753/6220609
        var drawableStart: Drawable? = null
        var drawableEnd: Drawable? = null
        var drawableBottom: Drawable? = null
        var drawableTop: Drawable? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawableStart = attributeArray.getDrawable(R.styleable.CompatTextView_drawableStart)
            drawableEnd = attributeArray.getDrawable(R.styleable.CompatTextView_drawableEnd)
            drawableBottom = attributeArray.getDrawable(R.styleable.CompatTextView_drawableBottom)
            drawableTop = attributeArray.getDrawable(R.styleable.CompatTextView_drawableTop)
        } else {
            val drawableStartId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableStart, -1)
            val drawableEndId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableEnd, -1)
            val drawableBottomId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableBottom, -1)
            val drawableTopId = attributeArray.getResourceId(R.styleable.CompatTextView_drawableTop, -1)

            if (drawableStartId != -1)
                drawableStart = AppCompatResources.getDrawable(context, drawableStartId)
            if (drawableEndId != -1)
                drawableEnd = AppCompatResources.getDrawable(context, drawableEndId)
            if (drawableBottomId != -1)
                drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId)
            if (drawableTopId != -1)
                drawableTop = AppCompatResources.getDrawable(context, drawableTopId)
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ->
                setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom)
            isLtr() -> setCompoundDrawablesWithIntrinsicBounds(drawableStart, drawableTop, drawableEnd, drawableBottom)
            else -> setCompoundDrawablesWithIntrinsicBounds(drawableEnd, drawableTop, drawableStart, drawableBottom)
        }

        attributeArray.recycle()
    }
}
