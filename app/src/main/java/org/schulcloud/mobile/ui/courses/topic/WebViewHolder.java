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
