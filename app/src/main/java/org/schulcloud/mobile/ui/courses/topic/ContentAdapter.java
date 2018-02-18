package org.schulcloud.mobile.ui.courses.topic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.schulcloud.mobile.BuildConfig;
import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCoursePresenter;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.WebUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ConfigPersistent
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.BaseViewHolder> {
    private static final String[] CONTENT_TYPES =
            {Contents.COMPONENT_TEXT, Contents.COMPONENT_RESOURCES, Contents.COMPONENT_GEOGEBRA};

    private List<Contents> mContents;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;
    @Inject
    UserDataManager mUserDataManger;

    @Inject
    public ContentAdapter() {
        mContents = new ArrayList<>();
    }

    public void setContents(@NonNull List<Contents> contents) {
        mContents = contents;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                return new TextViewHolder(
                        inflater.inflate(R.layout.item_content_text, parent, false));
            case 1:
                return new ResourcesViewHolder(
                        inflater.inflate(R.layout.item_content_resources, parent, false));
            case 2:
                return new GeogebraViewHolder(
                        inflater.inflate(R.layout.item_content_geogebra, parent, false));

            default:
                return new UnsupportedViewHolder(
                        inflater.inflate(R.layout.item_content_unsupported, parent, false));
        }
    }
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Contents content = mContents.get(position);
        boolean hidden = content.hidden != null ? content.hidden : false;
        ViewUtil.setVisibility(holder.itemView, !hidden);
        if (!hidden)
            holder.setContent(content);
    }
    @Override
    public int getItemCount() {
        return mContents.size();
    }
    @Override
    public int getItemViewType(int position) {
        String component = mContents.get(position).component.toLowerCase();
        for (int i = 0; i < CONTENT_TYPES.length; i++)
            if (component.equalsIgnoreCase(CONTENT_TYPES[i]))
                return i;
        return -1;
    }

    public static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(View itemView) {
            super(itemView);
        }

        @NonNull
        public Context getContext() {
            return itemView.getContext();
        }

        abstract void setContent(@NonNull Contents content);
    }

    class UnsupportedViewHolder extends BaseViewHolder {

        @BindView(R.id.contentUnsupported_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentUnsupported_tv_message)
        TextView vTv_message;

        UnsupportedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        void setContent(@NonNull Contents content) {
            vTv_title.setText(content.title);
            vTv_message.setText(getContext()
                    .getString(R.string.courses_contentUnsupported_message, content.component));
        }
    }
    class TextViewHolder extends BaseViewHolder {
        private static final String CONTENT_PREFIX = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta charset=\"utf-8\" />\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "  <style>\n"
                + "    table {\n"
                + "      table-layout: fixed;\n"
                + "      width: 100%;\n"
                + "    }\n"
                + "    * {\n"
                + "      max-width: 100%;\n"
                + "    }\n"
                + "  </style>\n"
                + "</head>\n"
                + "<body>";
        private static final String CONTENT_SUFFIX = "<script>\n"
                + "   for (tag of document.body.getElementsByTagName('*')) {\n"
                + "     tag.style.width = '';\n"
                + "     tag.style.height = '';\n"
                + "   }\n"
                + "  </script>\n"
                + "</body>\n"
                + "</html>\n";

        private final OkHttpClient CLIENT_INTERNAL;
        private final OkHttpClient CLIENT_EXTERNAL;

        @BindView(R.id.contentText_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentText_wv_content)
        WebView vWv_content;

        TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                WebView.setWebContentsDebuggingEnabled(true);

            CLIENT_INTERNAL = new OkHttpClient().newBuilder().addInterceptor(chain ->
                    chain.proceed(chain.request().newBuilder()
                            .addHeader(WebUtil.HEADER_COOKIE,
                                    "jwt=" + mUserDataManger.getAccessToken())
                            .build()))
                    .build();
            CLIENT_EXTERNAL = new OkHttpClient();
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void setContent(@NonNull Contents content) {
            vTv_title.setText(content.title);
            ViewUtil.setVisibility(vTv_title, !TextUtils.isEmpty(content.title));

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view,
                        WebResourceRequest request) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, request.getUrl()));
                    return true;
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
                        OkHttpClient okHttpClient;
                        if (url.startsWith(WebUtil.URL_BASE))
                            okHttpClient = CLIENT_INTERNAL;
                        else
                            okHttpClient = CLIENT_EXTERNAL;

                        Response response =
                                okHttpClient.newCall(new Request.Builder().url(url).build())
                                        .execute();
                        return new WebResourceResponse(
                                response.header(WebUtil.HEADER_CONTENT_TYPE,
                                        WebUtil.MIME_TEXT_PLAIN),
                                response.header(WebUtil.HEADER_CONTENT_TYPE,
                                        WebUtil.ENCODING_UTF_8),
                                response.body().byteStream()
                        );
                    } catch (Exception e) {
                        return null;
                    }
                }
            });
            vWv_content.loadDataWithBaseURL(WebUtil.URL_BASE,
                    CONTENT_PREFIX + (content.content.text != null ? content.content.text : "")
                            + CONTENT_SUFFIX, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
        }
    }
    public class ResourcesViewHolder extends BaseViewHolder {

        @Inject
        ResourcesAdapter mResourcesAdapter;

        @BindView(R.id.contentResources_rv)
        RecyclerView vRv;

        ResourcesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ((BaseActivity) itemView.getContext()).activityComponent().inject(this);

            vRv.setAdapter(mResourcesAdapter);
            vRv.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
        @Override
        void setContent(@NonNull Contents content) {
            if (content.content == null)
                return;

            mResourcesAdapter.setResources(content.content.resources);
        }
    }
    class GeogebraViewHolder extends BaseViewHolder {
        private static final String CONTENT_PREFIX = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "  <meta charset=\"utf-8\" />\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "  <script src=\"/vendor/geoGebra/deployggb.js\"></script>\n"
                + "</head>\n"
                + "<body>\n"
                + "  <div id=\"container\"></div>\n"
                + "  <script>\n"
                + "    var applet1 = new GGBApplet({material_id: \"";
        private static final String CONTENT_SUFFIX =
                "\", borderColor:\"transparent\", showFullscreenButton:true}, true);\n"
                        + "    applet1.inject('container');\n"
                        + "  </script>"
                        + "</body>\n"
                        + "</html>\n";

        @BindView(R.id.contentGeogebra_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentGeogebra_wv_content)
        WebView vWv_content;

        GeogebraViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                WebView.setWebContentsDebuggingEnabled(true);
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void setContent(@NonNull Contents content) {
            vTv_title.setText(content.title);
            ViewUtil.setVisibility(vTv_title, !TextUtils.isEmpty(content.title));

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.loadDataWithBaseURL(WebUtil.URL_BASE,
                    CONTENT_PREFIX + (content.content.materialId) + CONTENT_SUFFIX,
                    WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
        }
    }
}
