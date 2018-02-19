package org.schulcloud.mobile.ui.courses.topic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Resource;
import org.schulcloud.mobile.ui.base.BaseAdapter;
import org.schulcloud.mobile.util.WebUtil;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        return new ResourceViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ResourceViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Resource resource = mResources.get(position);

        holder.vCard.setOnClickListener(v ->
                WebUtil.resolveRedirect(resource.url, mUserDataManager.getAccessToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(url -> WebUtil.openUrl(getMainActivity(), url),
                                throwable -> {
                                    Log.e(TAG, "onBindViewHolder: ", throwable);
                                    DialogFactory.createGenericErrorDialog(context,
                                            R.string.courses_resources_error).show();
                                })
        );
        holder.vTv_title.setText(resource.title);
        holder.vTv_description.setText(resource.description);
        holder.vTv_client
                .setText(context.getString(R.string.courses_resource_client, resource.client));
    }
    @Override
    public int getItemCount() {
        return mResources.size();
    }

    class ResourceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.resource_card)
        CardView vCard;
        @BindView(R.id.resource_tv_title)
        TextView vTv_title;
        @BindView(R.id.resource_tv_description)
        TextView vTv_description;
        @BindView(R.id.resource_tv_client)
        TextView vTv_client;

        public ResourceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
