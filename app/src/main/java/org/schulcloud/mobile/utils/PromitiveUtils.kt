package org.schulcloud.mobile.utils

import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4

val Int.cssColor: String
    get() {
        val (a, r, g, b) = this
        return "rgba($r, $g, $b, ${a.toFloat() / 255})"
    }
