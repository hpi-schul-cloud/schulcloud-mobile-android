package org.schulcloud.mobile.controllers.file

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.ellipsizedSubstring
import org.schulcloud.mobile.views.CompatTextView

open class BreadcrumbsView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = R.attr.breadcrumbsViewStyle
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    companion object {
        val TAG: String = BreadcrumbsView::class.java.simpleName
    }

    var onPathSelected: ((String, String, String?) -> Unit)? = null

    private var textSize: Float = 0f
    private val partEndIndex = 15

    init {
        dividerDrawable = ResourcesCompat.getDrawable(context.resources,
                R.drawable.ic_chevron_right_black_24dp, context.theme)
        showDividers = SHOW_DIVIDER_MIDDLE

        context.withStyledAttributes(attrs, R.styleable.BreadcrumbsView,
                defStyleAttr, R.style.Widget_App_BreadcrumbsView) {
            textSize = getDimensionOrThrow(R.styleable.BreadcrumbsView_android_textSize)
        }
    }

    fun setPath(pathParts: List<String?>, refOwnerModel: String, owner: String, parent: String?, course: Course? = null) {
        removeAllViews()

        val title = when (refOwnerModel) {
            FileRepository.CONTEXT_MY_API -> context.getString(R.string.file_directory_my)
            FileRepository.CONTEXT_COURSE ->
                course?.name ?: context.getString(R.string.file_directory_course_unknown)
            else -> context.getString(R.string.file_directory_unknown)
        }
        addPartView(refOwnerModel, owner, null, title)

        for (part in pathParts)
            addPartView(refOwnerModel, owner, parent, part?.ellipsizedSubstring(0, partEndIndex))
    }

    fun setTextColor(@ColorInt color: Int) {
        for (child in children)
            (child as? TextView)?.setTextColor(color)
        dividerDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun addPartView(refOwnerModel: String, owner: String, parent: String?, title: String?) {
        addView(CompatTextView(context).also {
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            it.text = title
            it.setOnClickListener { onPathSelected?.invoke(refOwnerModel, owner, parent) }

            with(TypedValue()) {
                context.theme.resolveAttribute(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            android.R.attr.selectableItemBackgroundBorderless
                        else android.R.attr.selectableItemBackground,
                        this, true)
                it.setBackgroundResource(resourceId)
            }
        })
    }
}
