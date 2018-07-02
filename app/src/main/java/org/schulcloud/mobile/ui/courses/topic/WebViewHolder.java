package org.schulcloud.mobile.ui.courses.topic;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.util.WebUtil;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public abstract class WebViewHolder<T> extends BaseViewHolder<T> {
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

    protected final OkHttpClient CLIENT_INTERNAL;
    protected final OkHttpClient CLIENT_EXTERNAL;

    @Inject
    UserDataManager mUserDataManager;

    WebViewHolder(@NonNull UserDataManager userDataManager, @NonNull View itemView) {
        super(itemView);
        mUserDataManager = userDataManager;

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);

        CLIENT_INTERNAL = new OkHttpClient().newBuilder().addInterceptor(chain ->
                chain.proceed(chain.request().newBuilder()
                        .addHeader(WebUtil.HEADER_COOKIE,
                                "jwt=" + mUserDataManager.getAccessToken())
                        .build()))
                .build();
        CLIENT_EXTERNAL = new OkHttpClient();
    }
}
