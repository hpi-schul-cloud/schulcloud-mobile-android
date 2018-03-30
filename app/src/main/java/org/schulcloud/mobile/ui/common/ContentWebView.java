package org.schulcloud.mobile.ui.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.util.WebUtil;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Date: 3/30/2018
 */
public class ContentWebView extends WebView {
    private static final String TAG = ContentWebView.class.getSimpleName();

    // language=HTML
    public static final String CONTENT_TEXT_PREFIX = "<!DOCTYPE html>\n"
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
            + "<body>";
    // language=HTML
    public static final String CONTENT_TEXT_SUFFIX = "<script>\n"
            + "    for (tag of document.body.getElementsByTagName('*')) {\n"
            + "        tag.style.width = '';\n"
            + "        tag.style.height = '';\n"
            + "   }\n"
            + "</script>\n"
            + "</body>\n"
            + "</html>\n";

    @Inject
    UserDataManager mUserDataManager;

    private OkHttpClient mHttpClient;

    public ContentWebView(@NonNull Context context) {
        super(context, null);
        init();
    }
    public ContentWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }
    public ContentWebView(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @RequiresApi(21)
    public ContentWebView(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        if (!(getContext() instanceof MainActivity))
            throw new IllegalStateException("ContentWebView may only be attached to MainActivity");
        MainActivity mainActivity = (MainActivity) getContext();
        mainActivity.activityComponent().inject(this);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);

        mHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain -> {
            String host = chain.request().url().host();
            if (!host.endsWith(WebUtil.HOST_SCHULCLOUD_ORG) && !host
                    .endsWith(WebUtil.HOST_SCHUL_CLOUD_ORG))
                return chain.proceed(chain.request());

            // Add jwt to internal calls
            return chain.proceed(chain.request().newBuilder()
                    .addHeader(WebUtil.HEADER_COOKIE,
                            "jwt=" + mUserDataManager.getAccessToken())
                    .build());
        }).build();

        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                openUrl(Uri.parse(url));
                return true;
            }
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                openUrl(request.getUrl());
                return true;
            }
            private void openUrl(@NonNull Uri url) {
                WebUtil.openUrl(mainActivity, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                return handleRequest(url);
            }
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                    WebResourceRequest request) {
                return handleRequest(request.getUrl().toString());
            }
            @Nullable
            private WebResourceResponse handleRequest(@NonNull String url) {
                try {
                    Response response =
                            mHttpClient.newCall(new Request.Builder().url(url).build()).execute();
                    return new WebResourceResponse(
                            response.header(WebUtil.HEADER_CONTENT_TYPE, WebUtil.MIME_TEXT_PLAIN),
                            response.header(WebUtil.HEADER_CONTENT_TYPE, WebUtil.ENCODING_UTF_8),
                            response.body().byteStream());
                } catch (Exception e) {
                    return null;
                }
            }
        });
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setContent(@Nullable String content) {
        // Clear content to fix size if new size is smaller than current one
        loadDataWithBaseURL(null, null, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
        loadDataWithBaseURL(WebUtil.URL_BASE,
                CONTENT_TEXT_PREFIX + (content != null ? content : "") + CONTENT_TEXT_SUFFIX,
                WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
    }

    @NonNull
    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }
}
