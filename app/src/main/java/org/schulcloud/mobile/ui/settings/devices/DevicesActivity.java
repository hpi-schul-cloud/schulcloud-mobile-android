package org.schulcloud.mobile.ui.settings.devices;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.DevicesAdapter;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.Collections;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesActivity extends BaseActivity implements DevicesMvpView {
    private static final String EXTRA_TRIGGER_SYNC = "EXTRA_TRIGGER_SYNC";

    @Inject
    DevicesPresenter mDevicesPresenter;

    @Inject
    DevicesAdapter mDevicesAdapter;

    @BindView(R.id.devices_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        activityComponent().inject(this);
        ButterKnife.bind(this);

        Context context = this;

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.settings_title);

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC, true)) {
            startService(EventSyncService.getStartIntent(this));
            startService(DeviceSyncService.getStartIntent(this));
        }

        mRecyclerView.setAdapter(mDevicesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startService(DeviceSyncService.getStartIntent(context));

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mDevicesPresenter.loadDevices();

                    swiperefresh.setRefreshing(false);
                }, 3000);
            }
        });
        mDevicesPresenter.attachView(this);
        mDevicesPresenter.loadDevices();
    }

    @Override
    public void showDevices(@NonNull List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void reloadDevices() {
        mDevicesPresenter.loadDevices();
    }

    @Override
    public void showDevicesEmpty() {
        mDevicesAdapter.setDevices(Collections.emptyList());
        mDevicesAdapter.notifyDataSetChanged();
    }

    public void showDevicesError() {
        DialogFactory.createGenericErrorDialog(this, "Leider gab es ein problem beim Laden der Geräte");
    }
}
