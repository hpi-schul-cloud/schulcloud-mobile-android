package org.schulcloud.mobile.ui.courses.topic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import org.schulcloud.mobile.ui.courses.detailed.DetailedCoursePresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ConfigPersistent
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private static final String TAG = ContentAdapter.class.getSimpleName();
    private static final String CONTENT_PREFIX = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "  <meta charset=\"utf-8\" />\n"
            + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
            + "  <style>\n"
            + "    table {\n"
            + "        table-layout: fixed;\n"
            + "        width: 100%;\n"
            + "    }\n"
            + "  </style>\n"
            + "</head>\n"
            + "<body>";
    private static final String CONTENT_SUFFIX = "<script>\n"
            + " for (tag of document.body.getElementsByTagName('*'))\n"
            + "     tag.style.width = '';\n"
            + "  </script>\n"
            + "</body>\n"
            + "</html>\n";

    private List<Contents> mContent;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    UserDataManager mUserDataManger;

    @Inject
    public ContentAdapter() {
        mContent = new ArrayList<>();
    }

    public void setContent(@NonNull List<Contents> content) {
        mContent = content;
        notifyDataSetChanged();
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Contents contents = mContent.get(position);

        holder.vTv_title.setText(contents.title);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            WebView.setWebContentsDebuggingEnabled(true);

        if (!contents.component.equals(Contents.COMPONENT_TEXT))
            return;

        WebView webView = holder.vWv_content;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, request.getUrl()));
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
                    if (url.startsWith("https://schul-cloud.org"))
                        okHttpClient = new OkHttpClient().newBuilder().addInterceptor(chain -> chain
                                .proceed(chain.request().newBuilder().addHeader("Cookie",
                                        "jwt=" + mUserDataManger.getAccessToken()).build()))
                                .build();
                    else
                        okHttpClient = new OkHttpClient();

                    Response response =
                            okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
                    return new WebResourceResponse(
                            response.header("content-type", "text/plain"),
                            response.header("content-encoding", "utf-8"),
                            response.body().byteStream()
                    );
                } catch (Exception e) {
                    return null;
                }
            }
        });
        webView.loadDataWithBaseURL("https://schul-cloud.org",
                CONTENT_PREFIX + (contents.content.text != null ? contents.content.text : "")
                        + CONTENT_SUFFIX, "text/html",
                "utf-8", null);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.content_tv_title)
        TextView vTv_title;
        @BindView(R.id.content_wv_content)
        WebView vWv_content;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
