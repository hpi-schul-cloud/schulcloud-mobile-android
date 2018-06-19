package org.schulcloud.mobile.views

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.Request
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.utils.*


/**
 * Date: 6/11/2018
 */
open class ContentWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0)
    : WebView(context, attrs, defStyleAttr) {

    companion object {
        val TAG: String = ContentWebView::class.java.simpleName

        // language=HTML
        const val CONTENT_TEXT_PREFIX = ("<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "    <meta charset=\"utf-8\"/>\n"
                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "    <style>\n"
                + "        * {\n"
                + "            max-width: 100%;\n"
                + "        }\n"
                + "        body {\n"
                + "            margin: 0;\n"
                + "        }\n"
                + "        body > :first-child {\n"
                + "            margin-top: 0;\n"
                + "        }\n"
                + "        body > :nth-last-child(2) {\n"
                + "            margin-bottom: 0;\n"
                + "        }\n"
                + "        table {\n"
                + "            table-layout: fixed;\n"
                + "            width: 100%;\n"
                + "        }\n"
                + "        ul {\n"
                + "            -webkit-padding-start: 25px;\n"
                + "        }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>")
        // language=HTML
        const val CONTENT_TEXT_SUFFIX = ("<script>\n"
                + "    for (tag of document.body.getElementsByTagName('*')) {\n"
                + "        tag.style.width = '';\n"
                + "        tag.style.height = '';\n"
                + "   }\n"
                + "</script>\n"
                + "</body>\n"
                + "</html>\n")
    }

    init {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true)

        settings.javaScriptEnabled = true
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                openUrl(Uri.parse(url))
                return true
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                openUrl(request.url)
                return true
            }

            private fun openUrl(url: Uri) {
                openUrl(context, url)
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                return handleRequest(url)
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return handleRequest(request.url.toString())
            }

            private fun handleRequest(url: String): WebResourceResponse? {
                return try {
                    val response = HTTP_CLIENT.newCall(Request.Builder().url(url).build()).execute()
                    WebResourceResponse(
                            response.header(HEADER_CONTENT_TYPE)?.substringBefore(';'),
                            response.header(HEADER_CONTENT_ENCODING, ENCODING_UTF_8),
                            response.body()?.byteStream())
                } catch (e: Exception) {
                    null
                }
            }
        }
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun loadUrl(url: String?) {
        clear()
        super.loadUrl(url)
    }

    override fun loadUrl(url: String?, additionalHttpHeaders: MutableMap<String, String>?) {
        clear()
        super.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadData(data: String?, mimeType: String?, encoding: String?) {
        clear()
        super.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(baseUrl: String?, data: String?, mimeType: String?, encoding: String?, historyUrl: String?) {
        clear()
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    private fun clear() {
        // Clear content to fix size if new size is smaller than current one
        super.loadDataWithBaseURL(null, null, MIME_TEXT_HTML, ENCODING_UTF_8, null)
    }


    fun setContent(content: String?) {
        loadDataWithBaseURL(HOST, CONTENT_TEXT_PREFIX + (content ?: "") + CONTENT_TEXT_SUFFIX,
                MIME_TEXT_HTML, ENCODING_UTF_8, null)
    }
}
