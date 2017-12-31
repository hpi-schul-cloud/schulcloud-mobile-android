package org.schulcloud.mobile.ui.settings.devices;

import android.content.Intent;
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

        return view;
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
        DialogFactory.createGenericErrorDialog(getContext(), "Leider gab es ein problem beim Laden der Geräte");
    }
}
