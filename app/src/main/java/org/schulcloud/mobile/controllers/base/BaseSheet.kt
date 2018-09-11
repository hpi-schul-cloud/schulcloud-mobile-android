package org.schulcloud.mobile.controllers.base

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseSheet : BottomSheetDialogFragment() {
    val baseActivity: BaseActivity? get() = activity as? BaseActivity

    suspend fun requestPermission(permission: String): Boolean = baseActivity?.requestPermission(permission) ?: false
    suspend fun startActivityForResult(intent: Intent, options: Bundle? = null): Intent? {
        return baseActivity?.startActivityForResult(intent, options)
    }
}
