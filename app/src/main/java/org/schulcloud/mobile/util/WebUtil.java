package org.schulcloud.mobile.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.BuildConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Single;

/**
 * Date: 2/17/2018
 */
public final class WebUtil {
    private static final String TAG = WebUtil.class.getSimpleName();

    public static final String HEADER_COOKIE = "cookie";
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_CONTENT_ENCODING = "content-encoding";

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_HTML = "text/html";

    public static final String ENCODING_UTF_8 = "utf-8";

    public static final String URL_BASE_API = BuildConfig.URL_ENDPOINT;
    public static final String URL_BASE = "https://schul-cloud.org";

    @NonNull
    public static Single<Uri> resolveRedirect(@NonNull String url, @NonNull String accessToken) {
        if (url.charAt(0) != '/')
            return Single.just(Uri.parse(url));

        return Single.create(subscriber -> {
            OkHttpClient okHttpClient =
                    new OkHttpClient().newBuilder().addInterceptor(chain -> chain
                            .proceed(chain.request().newBuilder()
                                    .addHeader(HEADER_COOKIE, "jwt=" + accessToken).build()))
                            .build();
            try {
                Response response = okHttpClient
                        .newCall(new Request.Builder().url(PathUtil.combine(URL_BASE_API, url)).build())
                        .execute();
                subscriber.onSuccess(Uri.parse(response.request().url().toString()));
            } catch (IOException e) {
                Log.w(TAG, "Error resolving internal redirect", e);
                subscriber.onError(e);
            }
        });
    }
}
