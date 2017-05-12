package org.schulcloud.mobile.ui.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Device;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>{
    private List<Device> mDevices;

    @Inject
    public DevicesAdapter() {
        mDevices = new ArrayList<>();
    }

    public void setDevices(List<Device> devices) {
        mDevices = devices;
    }

    @Override
    public DevicesAdapter.DevicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        return new DevicesAdapter.DevicesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DevicesAdapter.DevicesViewHolder holder, int position) {
        Device device = mDevices.get(position);
        holder.nameTextView.setText(device.name);
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class DevicesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_name) TextView nameTextView;

        public DevicesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
