package org.schulcloud.mobile.utils

import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4

private const val COLOR_COMPONENT_MAX = 255

val Int.cssColor: String
    get() {
        val (a, r, g, b) = this
        return "rgba($r, $g, $b, ${a.toFloat() / COLOR_COMPONENT_MAX})"
    }
