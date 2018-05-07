package org.schulcloud.mobile.ui.courses.topic;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.data.model.responseBodies.GeogebraResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.ui.base.BaseViewHolder;
import org.schulcloud.mobile.ui.common.ContentWebView;
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
public class ContentAdapter extends BaseAdapter<BaseViewHolder<Contents>> {
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
    public BaseViewHolder<Contents> onCreateViewHolder(ViewGroup parent, int viewType) {
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
            case 3:
                return new EtherpadViewHolder(
                        inflater.inflate(R.layout.item_content_etherpad, parent, false));
            case 4:
                return new NexboardViewHolder(
                        inflater.inflate(R.layout.item_content_nexboard, parent, false));

            default:
                return new UnsupportedViewHolder(
                        inflater.inflate(R.layout.item_content_unsupported, parent, false));
        }
    }
    @Override
    public void onBindViewHolder(BaseViewHolder<Contents> holder, int position) {
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

    private static boolean isHidden(@NonNull Contents content) {
        return content.hidden != null ? content.hidden : false;
    }


    class UnsupportedViewHolder extends BaseViewHolder<Contents> {

        @BindView(R.id.contentUnsupported_ll_wrapper)
        LinearLayout vLl_wrapper;
        @BindView(R.id.contentUnsupported_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentUnsupported_tv_message)
        TextView vTv_message;

        UnsupportedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vLl_wrapper, isVisible);
            if (!isVisible)
                return;

            ViewUtil.setText(vTv_title, item.title);
            vTv_title.setText(item.title);
            vTv_message.setText(getContext()
                    .getString(R.string.courses_contentUnsupported_message, item.component));
        }
    }
    class TextViewHolder extends BaseViewHolder<Contents> {

        @BindView(R.id.contentText_ll_wrapper)
        LinearLayout vLl_wrapper;
        @BindView(R.id.contentText_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentText_cwv_content)
        ContentWebView vCwv_content;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vLl_wrapper, isVisible);
            if (!isVisible)
                return;

            ViewUtil.setText(vTv_title, item.title);

            vCwv_content.setContent(item.content.text);
        }
    }
    public class ResourcesViewHolder extends BaseViewHolder<Contents> {

        @Inject
        ResourcesAdapter mResourcesAdapter;

        @BindView(R.id.contentResources_ll_wrapper)
        LinearLayout vLl_wrapper;
        @BindView(R.id.contentResources_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentResources_rv)
        RecyclerView vRv;

        ResourcesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ((BaseActivity) itemView.getContext()).activityComponent().inject(this);

            vRv.setAdapter(mResourcesAdapter);
            vRv.setLayoutManager(new LinearLayoutManager(itemView.getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        }
        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vLl_wrapper, isVisible);
            if (!isVisible)
                return;

            ViewUtil.setText(vTv_title, item.title);

            mResourcesAdapter.setResources(item.content.resources);
        }
    }
    class GeogebraViewHolder extends BaseViewHolder<Contents> {
        private final String TAG = GeogebraViewHolder.class.getSimpleName();

        private static final String GEOGEBRA = "https://www.geogebra.org/m/";
        private static final String GEOGEBRA_API = "http://www.geogebra.org/api/json.php";
        // language=json
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
        // language=json
        private static final String GEOGEBRA_REQUEST_SUFFIX = "\" }\n"
                + "      ]\n"
                + "    },\n"
                + "    \"limit\": { \"-num\": \"1\" }\n"
                + "  }\n"
                + "}\n"
                + "}";
        private final OkHttpClient HTTP_CLIENT = new OkHttpClient();

        @BindView(R.id.contentGeogebra_cl_wrapper)
        ConstraintLayout vCl_wrapper;
        @BindView(R.id.contentGeogebra_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentGeogebra_iv_preview)
        ImageView vIv_preview;

        GeogebraViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            vCl_wrapper.setOnClickListener(v -> WebUtil
                    .openUrl(getMainActivity(),
                            Uri.parse(GEOGEBRA + getItem().content.materialId)));
        }

        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vCl_wrapper, isVisible);
            if (!isVisible)
                return;

            vTv_title.setText(item.title);

            loadPreviewImage();
        }
        private void loadPreviewImage() {
            ViewUtil.setVisibility(vIv_preview, true);
            Single.just(getItem().content.materialId)
                    .flatMap(materialId -> Single.<String>create(subscriber -> {
                        try {
                            Response responseRaw = HTTP_CLIENT.newCall(new Request.Builder()
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
                            Log.w(TAG, "Error retrieving preview URL", e);
                            subscriber.onError(e);
                        }
                    }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(uri -> Picasso.with(getContext()).load(uri).into(vIv_preview),
                            throwable -> {});
        }
    }
    class EtherpadViewHolder extends BaseViewHolder<Contents> {

        @BindView(R.id.contentEtherpad_cl_wrapper)
        ConstraintLayout vCl_wrapper;
        @BindView(R.id.contentEtherpad_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentEtherpad_tv_description)
        TextView vTv_description;
        @BindView(R.id.contentEtherpad_iv_open)
        ImageView vIv_open;
        @BindView(R.id.contentEtherpad_wv_content)
        WebView vWv_content;

        @SuppressLint("SetJavaScriptEnabled")
        EtherpadViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            vIv_open.setOnClickListener(v ->
                    WebUtil.openUrl(getMainActivity(), Uri.parse(getItem().content.url)));

            vWv_content.getSettings().setJavaScriptEnabled(true);
        }
        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vCl_wrapper, isVisible);
            if (!isVisible)
                return;

            vTv_title.setText(item.title);
            ViewUtil.setText(vTv_description, item.content.description);

            vWv_content
                    .loadDataWithBaseURL(null, null, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8,
                            null);
            vWv_content.loadUrl(getItem().content.url);
        }
    }
    class NexboardViewHolder extends BaseViewHolder<Contents> {
        private static final String URL_SUFFIX = "?username=Test&stickypad=false";

        @BindView(R.id.contentNexboard_cl_wrapper)
        ConstraintLayout vCl_wrapper;
        @BindView(R.id.contentNexboard_tv_title)
        TextView vTv_title;
        @BindView(R.id.contentNexboard_tv_description)
        TextView vTv_description;
        @BindView(R.id.contentNexboard_iv_open)
        ImageView vIv_open;
        @BindView(R.id.contentNexboard_wv_content)
        WebView vWv_content;

        @SuppressLint("SetJavaScriptEnabled")
        NexboardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            vIv_open.setOnClickListener(v -> WebUtil
                    .openUrl(getMainActivity(), Uri.parse(getItem().content.url + URL_SUFFIX)));

            vWv_content.getSettings().setJavaScriptEnabled(true);
        }
        @Override
        public void onItemSet(@NonNull Contents item) {
            boolean isVisible = !isHidden(item);
            ViewUtil.setVisibility(vCl_wrapper, isVisible);
            if (!isVisible)
                return;

            vTv_title.setText(item.title);
            ViewUtil.setText(vTv_description, item.content.description);

            vWv_content
                    .loadDataWithBaseURL(null, null, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8,
                            null);
            vWv_content.loadUrl(getItem().content.url + URL_SUFFIX);
        }
    }
}
