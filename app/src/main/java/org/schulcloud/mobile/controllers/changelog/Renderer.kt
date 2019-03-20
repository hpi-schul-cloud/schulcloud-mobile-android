package org.schulcloud.mobile.controllers.changelog

import android.content.Context
import android.graphics.Typeface
import androidx.core.view.updatePadding
import com.michaelflisar.changelog.ChangelogBuilder
import com.michaelflisar.changelog.classes.ChangelogRenderer
import com.michaelflisar.changelog.internal.ChangelogRecyclerViewAdapter
import com.michaelflisar.changelog.items.ItemRow
import org.schulcloud.mobile.utils.dpToPx

class Renderer : ChangelogRenderer() {
    override fun bindRow(
        adapter: ChangelogRecyclerViewAdapter?,
        context: Context?,
        viewHolder: ViewHolderRow?,
        row: ItemRow?,
        builder: ChangelogBuilder?
    ) {
        super.bindRow(adapter, context, viewHolder, row, builder)
        viewHolder ?: return
        row ?: return

        when (row.tag) {
            is IntroTag -> {
                viewHolder.tvText.setTypeface(viewHolder.tvText.typeface, Typeface.BOLD)
                viewHolder.tvText.updatePadding(bottom = 8.dpToPx())
            }
            else -> {
                viewHolder.tvText.setTypeface(viewHolder.tvText.typeface, Typeface.NORMAL)
                viewHolder.tvText.updatePadding(bottom = 0.dpToPx())
            }
        }
    }
}
