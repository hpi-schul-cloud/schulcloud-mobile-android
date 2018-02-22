package org.schulcloud.mobile.ui.courses.topic;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Resource;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.WebUtil;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.schulcloud.mobile.ui.courses.topic.ContentAdapter.TextViewHolder.CONTENT_PREFIX;
import static org.schulcloud.mobile.ui.courses.topic.ContentAdapter.TextViewHolder.CONTENT_SUFFIX;

/**
 * Date: 2/17/2018
 */
public class ResourcesAdapter extends BaseAdapter<ResourcesAdapter.ResourceViewHolder> {
    private static final String TAG = ResourcesAdapter.class.getSimpleName();

    private final UserDataManager mUserDataManager;

    private List<Resource> mResources;

    @Inject
    public ResourcesAdapter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
        mResources = new ArrayList<>();
    }

    public void setResources(@NonNull List<Resource> resources) {
        mResources = resources;
        notifyDataSetChanged();
    }

    @Override
    public ResourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resource, parent, false);
        return new ResourceViewHolder(mUserDataManager, itemView);
    }
    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        holder.setItem(mResources.get(position));
    }
    @Override
    public int getItemCount() {
        return mResources.size();
    }

    class ResourceViewHolder extends WebViewHolder<Resource> {

        @BindView(R.id.resource_card)
        CardView vCard;
        @BindView(R.id.resource_tv_title)
        TextView vTv_title;
        @BindView(R.id.resource_pwv_description)
        PassiveWebView vPwv_description;
        @BindView(R.id.resource_tv_client)
        TextView vTv_client;

        public ResourceViewHolder(@NonNull UserDataManager userDataManager,
                @NonNull View itemView) {
            super(userDataManager, itemView);
            ButterKnife.bind(this, itemView);

            vCard.setOnClickListener(v ->
                    WebUtil.resolveRedirect(getItem().url, mUserDataManager.getAccessToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(url -> WebUtil.openUrl(getMainActivity(), url),
                                    throwable -> {
                                        Log.e(TAG, "onBindViewHolder: ", throwable);
                                        DialogFactory.createGenericErrorDialog(getContext(),
                                                R.string.courses_resources_error).show();
                                    }));
            vPwv_description.setOnTouchListener(null);
        }

        @Override
        void onItemSet(@NonNull Resource item) {
            vTv_title.setText(item.title);
            ViewUtil.setText(vTv_title, item.title);

            vPwv_description.getSettings().setJavaScriptEnabled(true);
            vPwv_description.setWebViewClient(new WebViewClient() {
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
            });
            vPwv_description.loadDataWithBaseURL(WebUtil.URL_BASE,
                    CONTENT_PREFIX + (item.description != null ? item.description : "")
                            + CONTENT_SUFFIX, WebUtil.MIME_TEXT_HTML, WebUtil.ENCODING_UTF_8, null);
            vTv_client
                    .setText(getContext().getString(R.string.courses_resource_client, item.client));
        }
    }
}
