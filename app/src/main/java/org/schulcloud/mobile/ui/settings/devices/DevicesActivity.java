package org.schulcloud.mobile.ui.settings.devices;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.settings.DevicesAdapter;
import org.schulcloud.mobile.util.dialogs.DialogFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesFragment extends BaseFragment implements DevicesMvpView {

    @Inject
    DevicesAdapter mDevicesAdapter;
    @Inject
    DevicesPresenter mDevicesPresenter;
    @BindView(R.id.btn_create_device)
    Button createDeviceBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        activityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_news, container, false);
        ButterKnife.bind(this, view);

        mDevicesPresenter.attachView(this);
        createDeviceBtn.setOnClickListener(listener -> mDevicesPresenter.registerDevice());
        mDevicesPresenter.loadDevices();
    }

    @Override
    public void showDevices(@NonNull List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void reloadDevices() {
    }

    @Override
    public void showDevicesEmpty() {
        
    }

    @Override
    public void showDevicesError() {
        DialogFactory.createGenericErrorDialog(this, "Leider gab es ein problem beim Laden der Geräte");
    }
}
