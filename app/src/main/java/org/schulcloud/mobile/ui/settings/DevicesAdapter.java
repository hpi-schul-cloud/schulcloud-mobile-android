package org.schulcloud.mobile.ui.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.settings.devices.DevicesPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@ConfigPersistent
public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder> {
    @Inject
    SettingsPresenter mSettingsPresenter;
    private final NotificationDataManager mNotificationDataManager;
    DevicesPresenter mDevicesPresenter;
    private List<Device> mDevices;

    @Inject
    public DevicesAdapter(NotificationDataManager dataManager) {
        mDevices = new ArrayList<>();
        mNotificationDataManager = dataManager;
    }

    public void setDevices(@NonNull List<Device> devices) {
        mDevices = devices;
        notifyDataSetChanged();
    }

    @Override
    public DevicesAdapter.DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new DevicesAdapter.DevicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DevicesAdapter.DevicesViewHolder holder, int position) {
        Device device = mDevices.get(position);
        holder.nameTextView.setText(device.name);
        holder.tokenText.setText(device.token);

        if (mNotificationDataManager.getPreferencesHelper().getMessagingToken()
                .equals(holder.tokenText.getText().toString())) {
            holder.awesomeTextView.setFontAwesomeIcon("fa_trash");
            holder.awesomeTextView.setOnClickListener(v -> mDevicesPresenter.deleteDevice(device));
        }
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class DevicesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_item_icon)
        AwesomeTextView awesomeTextView;
        @BindView(R.id.text_name)
        TextView nameTextView;
        @BindView(R.id.token)
        TextView tokenText;

        public DevicesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        List<Device> mDevices;
        Context mContext;

        public void initExpandableListAdapter(Context context, List<Device> devices){
            mDevices = devices;
            mContext = context;
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public int getChildrenCount(int group) {
            return mDevices.size();
        }

        @Override
        public Object getGroup(int group) {
            return group;
        }

        @Override
        public Object getChild(int group, int child) {
            return child;
        }

        @Override
        public long getGroupId(int groupPos) {
            return groupPos;
        }

        @Override
        public long getChildId(int group, int childPos) {
            return childPos;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int group, boolean isExpanded, View view, ViewGroup viewGroup) {

            if(view == null){
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_header_devices,null);
            }

            return view;
        }

        @Override
        public View getChildView(int group, int child, boolean isExpanded, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_device,viewGroup,false);
                DevicesViewHolder holder = new DevicesAdapter.DevicesViewHolder(view);
                holder.nameTextView.setText(mDevices.get(child).name);
                holder.tokenText.setText(mDevices.get(child).token);
                holder.awesomeTextView.setFontAwesomeIcon("fa_trash");
                holder.awesomeTextView.setOnClickListener(v -> mDevicesPresenter.deleteDevice(mDevices.get(child)));
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
