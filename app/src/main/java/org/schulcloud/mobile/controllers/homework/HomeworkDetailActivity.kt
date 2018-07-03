package org.schulcloud.mobile.controllers.homework

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_homework_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.HomeworkViewModelFactory

class HomeworkDetailActivity : BaseActivity() {

    companion object {
        val TAG: String = HomeworkDetailActivity::class.java.simpleName
        const val EXTRA_ID = "EXTRA_ID"
    }

    private lateinit var homeworkViewModel: HomeworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homework_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        homeworkViewModel = ViewModelProviders.of(this, HomeworkViewModelFactory(intent.getStringExtra(EXTRA_ID))).get(HomeworkViewModel::class.java)
        homeworkViewModel.getHomework().observe(this, Observer<Homework> {
            it?.let { onHomeworkUpdate(it) }
        })
    }

    private fun onHomeworkUpdate(homework: Homework) {
        homework_detail_title.text = homework.title

        homework.courseId?.let {
            homework_detail_course_title.text = it.name
            homework_detail_course_color.setColorFilter(Color.parseColor(it.color))
        }

        homework.dueDate?.let {
            val dueTextAndColorId = homework.getDueTextAndColorId()
            homework_detail_duetill.text = dueTextAndColorId.first
            homework_detail_duetill.setTextColor(dueTextAndColorId.second)
        }

        homework.description?.let {
            homework_detail_description.apply {
                webViewClient = AuthorizedWebViewClient.getWithContext(this@HomeworkDetailActivity)

                // format the displayed content, enable zoom
                settings.builtInZoomControls = true
                settings.displayZoomControls = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.defaultFontSize = 18
                val formattedDescription = ("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=yes\" /></head>"
                                            + it
                                            + "</body></html>")
                loadData(formattedDescription, "text/html", "UTF-8")
            }
        }
        homework_detail_description.setBackgroundColor(Color.TRANSPARENT)
    }

    /**
     * Allows loading images from SchulCloud database with user access token
     */
    class AuthorizedWebViewClient : WebViewClient() {

        companion object {
            fun getWithContext(context: Context): AuthorizedWebViewClient {
                val client = AuthorizedWebViewClient()
                client.context = context
                return client
            }
        }

        var context: Context? = null

        private val client: OkHttpClient by lazy {
            OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val builder = chain.request().newBuilder()
                        if (UserRepository.isAuthorized) {
                            builder.header("cookie", "jwt=" + UserRepository.token)
                        }
                        chain.proceed(builder.build())
                    }.build()
        }

        /**
         * Opens external link in browser
         */
        @Suppress("OverridingDeprecatedMember")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context?.startActivity(intent)
            return true
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val intent = Intent(Intent.ACTION_VIEW, request?.url)
            context?.startActivity(intent)
            return true
        }

        @Suppress("OverridingDeprecatedMember")
        override fun shouldInterceptRequest(view: WebView?, url: String): WebResourceResponse? {
            return getNewResponse(url)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest): WebResourceResponse? {
            return getNewResponse(request.url.toString())
        }

        /**
         * Gets a response using the user access token
         *
         * @return a WebResourceResponse containing the data received by a call with the token
         */
        private fun getNewResponse(url: String): WebResourceResponse? {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                return WebResourceResponse(
                        response.header("content-type", response.body()?.contentType()?.type()),
                        response.header("content-encoding", "utf-8"),
                        response.body()?.byteStream())
            } catch (e: Exception) {
                return null
            }
        }
    }
}