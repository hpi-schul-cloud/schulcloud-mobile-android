package org.schulcloud.mobile.controllers.base

import android.content.pm.PackageManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.asUri
import org.schulcloud.mobile.utils.openUrl
import org.schulcloud.mobile.utils.setup
import org.schulcloud.mobile.utils.shareLink
import java.util.*
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.properties.Delegates


abstract class BaseActivity : AppCompatActivity() {
    open var url: String? = null
    var swipeRefreshLayout by Delegates.observable<SwipeRefreshLayout?>(null) { _, _, new ->
        new?.setup()
        new?.setOnRefreshListener { performRefresh() }
    }

    private val permissionRequests: MutableList<Continuation<Boolean>>
            by lazy { LinkedList<Continuation<Boolean>>() }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.activity_action_share -> shareLink(url!!, supportActionBar?.title)
        // TODO: Remove when deep linking is readded
            R.id.activity_action_openInBrowser -> openUrl(url.asUri())
            R.id.activity_action_refresh -> performRefresh()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    protected fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    protected open suspend fun refresh() {}
    protected fun performRefresh() {
        swipeRefreshLayout?.isRefreshing = true
        launch {
            withContext(UI) { refresh() }
            withContext(UI) { swipeRefreshLayout?.isRefreshing = false }
        }
    }

    suspend fun requestPermission(permission: String): Boolean = suspendCoroutine {cont ->
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED){
            cont.resume(true)
            return@suspendCoroutine
        }

        permissionRequests.add(cont)
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequests.size - 1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // Request not from this class
        if (requestCode >= permissionRequests.size) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        //The request was interrupted
        if (permissions.isEmpty()) {
            permissionRequests[requestCode].resume(false)
            return
        }

        permissionRequests[requestCode].resume(grantResults[0] == PackageManager.PERMISSION_GRANTED)
    }
}
