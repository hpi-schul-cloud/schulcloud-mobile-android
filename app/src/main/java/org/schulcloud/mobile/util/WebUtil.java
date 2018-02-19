package org.schulcloud.mobile.util;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardFragment;
import org.schulcloud.mobile.ui.files.FilesFragment;
import org.schulcloud.mobile.ui.files.overview.FileOverviewFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.ui.news.NewsFragment;
import org.schulcloud.mobile.ui.news.detailed.DetailedNewsFragment;

import java.io.IOException;
import java.util.List;

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
    public static final String MIME_APPLICATION_JSON = "application/json";

    public static final String ENCODING_UTF_8 = "utf-8";

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    public static final String HOST_SCHULCLOUD_ORG = "schulcloud.org";
    public static final String HOST_SCHUL_CLOUD_ORG = "schul-cloud.org";

    public static final String URL_BASE_API = BuildConfig.URL_ENDPOINT;
    public static final String URL_BASE = SCHEME_HTTPS + "://" + HOST_SCHUL_CLOUD_ORG;

    // Internal paths
    public static final String PATH_INTERNAL_DASHBOARD = "dashboard";

    public static final String PATH_INTERNAL_NEWS = "news";

    public static final String PATH_INTERNAL_COURSES = "courses";

    public static final String PATH_INTERNAL_HOMEWORK = "homework";
    public static final String PATH_INTERNAL_HOMEWORK_ASKED = "asked";
    public static final String PATH_INTERNAL_HOMEWORK_PRIVATE = "private";
    public static final String PATH_INTERNAL_HOMEWORK_ARCHIVE = "archive";
    public static final String[] PATHS_INTERNAL_HOMEWORK = {
            PATH_INTERNAL_HOMEWORK_ASKED,
            PATH_INTERNAL_HOMEWORK_PRIVATE,
            PATH_INTERNAL_HOMEWORK_ARCHIVE};

    public static final String PATH_INTERNAL_FILES = "files";
    public static final String PATH_INTERNAL_FILES_MY = "my";
    public static final String PATH_INTERNAL_FILES_COURSES = "courses";
    public static final String PATH_INTERNAL_FILES_SHARED = "shared";

    public static final String[] PATHS_INTERNAL = {
            PATH_INTERNAL_DASHBOARD,
            PATH_INTERNAL_NEWS,
            PATH_INTERNAL_COURSES,
            PATH_INTERNAL_HOMEWORK,
            PATH_INTERNAL_FILES};

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
                        .newCall(new Request.Builder().url(PathUtil.combine(URL_BASE_API, url))
                                .build())
                        .execute();
                subscriber.onSuccess(Uri.parse(response.request().url().toString()));
            } catch (IOException e) {
                Log.w(TAG, "Error resolving internal redirect", e);
                subscriber.onError(e);
            }
        });
    }

    @NonNull
    public static CustomTabsIntent newCustomTab(@NonNull Context context) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.hpiRed));
        builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right);
        return builder.build();
    }

    public static void openUrl(@NonNull MainActivity mainActivity, @NonNull Uri url) {
        openUrl(mainActivity, mainActivity.getCurrentFragment(), url);
    }
    public static void openUrl(@NonNull MainActivity mainActivity, @NonNull MainFragment fragment,
            @NonNull Uri url) {
        Log.i(TAG, "Opening url: " + url);
        String scheme = url.getScheme().toLowerCase();
        String host = url.getHost().toLowerCase();
        List<String> path = url.getPathSegments();
        String pathPrefix = null;
        String pathEnd = null;
        if (!path.isEmpty()) {
            pathPrefix = path.get(0);
            pathEnd = path.get(path.size() - 1).toLowerCase();
        }

        // Internal links can be handled by the app/
        if ((scheme.equals(SCHEME_HTTP) || scheme.equals(SCHEME_HTTPS))
                && (host.equals(HOST_SCHULCLOUD_ORG) || host.equals(HOST_SCHUL_CLOUD_ORG))
                && path.size() >= 0
                && ListUtils.contains(PATHS_INTERNAL, pathPrefix)) {
            MainFragment newFragment = null;
            assert pathPrefix != null;
            switch (pathPrefix) {
                case PATH_INTERNAL_DASHBOARD:
                    if (!(fragment instanceof DashboardFragment))
                        mainActivity.addFragment(fragment, DashboardFragment.newInstance());
                    return;

                case PATH_INTERNAL_NEWS:
                    if (path.size() == 1)
                        newFragment = NewsFragment.newInstance();
                    else if (path.size() == 2)
                        newFragment = DetailedNewsFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_COURSES:
                    if (path.size() == 1)
                        newFragment = CourseFragment.newInstance();
                    else if (path.size() == 2)
                        newFragment = DetailedCourseFragment.newInstance(pathEnd);
                    //else if (path.size() == 4)
                    //    newFragment = TopicFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_HOMEWORK:
                    if (path.size() == 1)
                        newFragment = HomeworkFragment.newInstance();
                    else if (path.size() == 2 && !ListUtils
                            .contains(PATHS_INTERNAL_HOMEWORK, pathEnd))
                        newFragment = DetailedHomeworkFragment.newInstance(pathEnd);
                    break;

                case PATH_INTERNAL_FILES:
                    if (path.size() == 1)
                        newFragment = FileOverviewFragment.newInstance(false);
                    break;
            }
            Log.i(TAG, "Chosen fragment: " + newFragment);
            if (newFragment != null) {
                mainActivity.addFragment(fragment, newFragment);
                return;
            }
        }

        newCustomTab(mainActivity).launchUrl(mainActivity, url);
    }
}
