package org.schulcloud.mobile.viewmodels

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.coroutines.experimental.Continuation

class BaseViewModel : ViewModel() {
    private val permissionRequests: MutableList<Continuation<Boolean>>
            by lazy { LinkedList<Continuation<Boolean>>() }

    private val activityRequests: MutableList<Continuation<Intent?>>
            by lazy { LinkedList<Continuation<Intent?>>() }

    fun addPermissionRequest(request: Continuation<Boolean>): Int {
        permissionRequests += request
        return activityRequests.size - 1
    }

    fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (requestCode >= permissionRequests.size) return false

        if (permissions.isEmpty())
            permissionRequests[requestCode].resume(
                    if (permissions.isEmpty()) false
                    else grantResults[0] == PackageManager.PERMISSION_GRANTED)
        return true
    }


    fun addActivityRequest(request: Continuation<Intent?>): Int {
        activityRequests += request
        return activityRequests.size - 1
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode >= activityRequests.size) return false

        activityRequests[requestCode].resume(data.takeIf { resultCode != Activity.RESULT_OK })
        return true
    }
}
