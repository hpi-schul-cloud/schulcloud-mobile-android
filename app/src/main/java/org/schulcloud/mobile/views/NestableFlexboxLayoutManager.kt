package org.schulcloud.mobile.views

import android.content.Context
import com.google.android.flexbox.FlexboxLayoutManager

class NestableFlexboxLayoutManager(context: Context) : FlexboxLayoutManager(context) {
    override fun isAutoMeasureEnabled() = true
}
