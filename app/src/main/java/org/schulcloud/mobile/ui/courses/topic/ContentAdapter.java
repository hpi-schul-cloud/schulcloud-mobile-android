package org.schulcloud.mobile.ui.courses.topic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.data.model.responseBodies.GeogebraResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCoursePresenter;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.WebUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class ContentAdapter extends BaseAdapter<BaseViewHolder> {
    private static final String[] CONTENT_TYPES = {
            Contents.COMPONENT_TEXT,
            Contents.COMPONENT_RESOURCES,
            Contents.COMPONENT_GEOGEBRA,
            Contents.COMPONENT_ETHERPAD,
            Contents.COMPONENT_NEXBOARD};

    private List<Contents> mContents;

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;
    @Inject
    UserDataManager mUserDataManger;
    @Inject
    Gson mGson;

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
                return new TextViewHolder(mUserDataManger,
                        inflater.inflate(R.layout.item_content_text, parent, false));
            case 1:
                return new ResourcesViewHolder(
                        inflater.inflate(R.layout.item_content_resources, parent, false));
            case 2:
                return new GeogebraViewHolder(mUserDataManger,
                        inflater.inflate(R.layout.item_content_geogebra, parent, false));
            case 3:
                return new EtherpadViewHolder(mUserDataManger,
                        inflater.inflate(R.layout.item_content_etherpad, parent, false));
            case 4:
                return new NexboardViewHolder(mUserDataManger,
                        inflater.inflate(R.layout.item_content_nexboard, parent, false));

            default:
                return new UnsupportedViewHolder(
                        inflater.inflate(R.layout.item_content_unsupported, parent, false));
        }
    }
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setItem(mContents.get(position));
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


    class UnsupportedViewHolder extends BaseViewHolder<Contents> {

        @BindView(R.id.contentUnsupported_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentUnsupported_tv_message)
        TextView vTv_message;

        UnsupportedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        void onItemSet(@NonNull Contents item) {
            vTv_title.setText(item.title);
            vTv_message.setText(getContext()
                    .getString(R.string.courses_contentUnsupported_message, item.component));
        }
    }
    class TextViewHolder extends WebViewHolder<Contents> {
        public static final String CONTENT_PREFIX = "<!DOCTYPE html>\n"
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
        public static final String CONTENT_SUFFIX = "<script>\n"
                + "   for (tag of document.body.getElementsByTagName('*')) {\n"
                + "     tag.style.width = '';\n"
                + "     tag.style.height = '';\n"
                + "   }\n"
                + "  </script>\n"
                + "</body>\n"
                + "</html>\n";

        @BindView(R.id.contentText_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentText_wv_content)
        WebView vWv_content;

        TextViewHolder(@NonNull UserDataManager userDataManager, @NonNull View itemView) {
            super(userDataManager, itemView);
            ButterKnife.bind(this, itemView);
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void onItemSet(@NonNull Contents item) {
            ViewUtil.setText(vTv_title, item.title);

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.setWebViewClient(new WebViewClient() {
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
                    WebUtil.openUrl(getMainActivity(), url);
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
                    CONTENT_PREFIX + (item.content.text != null ? item.content.text : "")
                            + CONTENT_SUFFIX, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
        }
    }
    public class ResourcesViewHolder extends BaseViewHolder<Contents> {

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
        void onItemSet(@NonNull Contents item) {
            if (item.content == null)
                return;

            mResourcesAdapter.setResources(item.content.resources);
        }
    }
    class GeogebraViewHolder extends WebViewHolder<Contents> {
        private final String TAG = GeogebraViewHolder.class.getSimpleName();

        private static final String GEOGEBRA = "https://www.geogebra.org/m/";
        private static final String GEOGEBRA_API = "http://www.geogebra.org/api/json.php";
        private static final String GEOGEBRA_REQUEST_PREFIX = "{ \"request\": {\n"
                + "  \"-api\": \"1.0.0\",\n"
                + "  \"task\": {\n"
                + "    \"-type\": \"fetch\",\n"
                + "    \"fields\": {\n"
                + "      \"field\": [\n"
                + "        { \"-name\": \"preview_url\" }\n"
                + "      ]\n"
                + "    },\n"
                + "    \"filters\" : {\n"
                + "      \"field\": [\n"
                + "        { \"-name\":\"id\", \"#text\":\"";
        private static final String GEOGEBRA_REQUEST_SUFFIX = "\" }\n"
                + "      ]\n"
                + "    },\n"
                + "    \"limit\": { \"-num\": \"1\" }\n"
                + "  }\n"
                + "}\n"
                + "}";

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
        @BindView(R.id.contentGeogebra_iv_open)
        ImageView vIv_open;
        @BindView(R.id.contentGeogebra_wv_content)
        WebView vWv_content;
        @BindView(R.id.contentGeogebra_iv_preview)
        ImageView vIv_preview;
        @BindView(R.id.contentGeogebra_pb_loading)
        ProgressBar vPb_loading;

        GeogebraViewHolder(@NonNull UserDataManager userDataManager, @NonNull View itemView) {
            super(userDataManager, itemView);
            ButterKnife.bind(this, itemView);

            vIv_open.setOnClickListener(v -> WebUtil
                    .openUrl(getMainActivity(), Uri.parse(GEOGEBRA + getItem().content.materialId)));

            vIv_preview.setOnClickListener(v -> load());
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void onItemSet(@NonNull Contents item) {
            vTv_title.setText(item.title);

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    ViewUtil.setVisibility(vIv_preview, false);
                    ViewUtil.setVisibility(vPb_loading, false);
                    ViewUtil.setVisibility(vWv_content, true);
                }
            });

            loadPreviewImage();
        }

        private void loadPreviewImage() {
            ViewUtil.setVisibility(vIv_preview, true);
            ViewUtil.setVisibility(vPb_loading, false);
            ViewUtil.setVisibility(vWv_content, false);
            Single.just(getItem().content.materialId)
                    .flatMap(materialId -> Single.<String>create(subscriber -> {
                        try {
                            Response responseRaw = CLIENT_EXTERNAL.newCall(new Request.Builder()
                                    .url(GEOGEBRA_API)
                                    .post(RequestBody
                                            .create(MediaType.parse(WebUtil.MIME_APPLICATION_JSON),
                                                    GEOGEBRA_REQUEST_PREFIX + materialId
                                                            + GEOGEBRA_REQUEST_SUFFIX))
                                    .build()).execute();
                            GeogebraResponse response =
                                    mGson.fromJson(responseRaw.body().charStream(),
                                            GeogebraResponse.class);
                            subscriber.onSuccess(response.responses.response.item.previewUrl);
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving preview URL", e);
                            subscriber.onError(e);
                        }
                    }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> Picasso.with(getContext()).load(uri).into(vIv_preview),
                            throwable -> load());
        }
        private void load() {
            ViewUtil.setVisibility(vPb_loading, true);
            vWv_content.loadDataWithBaseURL(WebUtil.URL_BASE,
                    CONTENT_PREFIX + getItem().content.materialId + CONTENT_SUFFIX,
                    WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
        }
    }
    class EtherpadViewHolder extends WebViewHolder<Contents> {

        @BindView(R.id.contentEtherpad_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentEtherpad_tv_description)
        TextView vTv_description;
        @BindView(R.id.contentEtherpad_iv_open)
        ImageView vIv_open;
        @BindView(R.id.contentEtherpad_wv_content)
        WebView vWv_content;

        EtherpadViewHolder(@NonNull UserDataManager userDataManager, @NonNull View itemView) {
            super(userDataManager, itemView);
            ButterKnife.bind(this, itemView);

            vIv_open.setOnClickListener(v ->
                    WebUtil.openUrl(getMainActivity(), Uri.parse(getItem().content.url)));
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void onItemSet(@NonNull Contents item) {
            vTv_title.setText(item.content.title);

            ViewUtil.setText(vTv_description, item.content.description);

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.loadUrl(getItem().content.url);
        }
    }
    class NexboardViewHolder extends WebViewHolder<Contents> {
        private static final String URL_SUFFIX = "?username=Test&stickypad=false";

        @BindView(R.id.contentNexboard_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentNexboard_tv_description)
        TextView vTv_description;
        @BindView(R.id.contentNexboard_iv_open)
        ImageView vIv_open;
        @BindView(R.id.contentNexboard_wv_content)
        WebView vWv_content;

        NexboardViewHolder(@NonNull UserDataManager userDataManager, @NonNull View itemView) {
            super(userDataManager, itemView);
            ButterKnife.bind(this, itemView);

            vIv_open.setOnClickListener(v -> WebUtil
                    .openUrl(getMainActivity(), Uri.parse(getItem().content.url + URL_SUFFIX)));
        }
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        void onItemSet(@NonNull Contents item) {
            vTv_title.setText(item.content.title);

            ViewUtil.setText(vTv_description, item.content.description);

            vWv_content.getSettings().setJavaScriptEnabled(true);
            vWv_content.loadUrl(getItem().content.url + URL_SUFFIX);
        }
    }
}
