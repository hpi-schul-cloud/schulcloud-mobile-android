package org.schulcloud.mobile.ui.settings.devices;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.settings.DevicesAdapter;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.Collections;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesFragment extends BaseFragment implements DevicesMvpView {
    private static final String ARGUMENT_TRIGGER_SYNC = "EXTRA_TRIGGER_SYNC";

    @Inject
    DevicesPresenter mDevicesPresenter;

    @Inject
    DevicesAdapter mDevicesAdapter;

    @BindView(R.id.devices_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;

    public static DevicesFragment newInstance() {
        return newInstance(true);
    }
    /**
      * Creates a new instance of this fragment.
      *
      * @param triggerDataSyncOnCreate Allows disabling the background sync service onCreate. Should
      *                                only be set to false during testing.
      * @return The new instance
      */

    public static DevicesFragment newInstance(boolean triggerDataSyncOnCreate) {
        DevicesFragment mDevicesFragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_TRIGGER_SYNC, triggerDataSyncOnCreate);
        mDevicesFragment.setArguments(args);
        return mDevicesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);

        if(getArguments().getBoolean(ARGUMENT_TRIGGER_SYNC)) {
            getActivity().startService(DeviceSyncService.getStartIntent(getContext()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_devices,container,false);
        ButterKnife.bind(this,container);

        mRecyclerView.setAdapter(mDevicesAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().startService(DeviceSyncService.getStartIntent(getContext()));

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mDevicesPresenter.loadDevices();

                    swiperefresh.setRefreshing(false);
                }, 3000);
            }
        });
        mDevicesPresenter.attachView(this);
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
        mDevicesPresenter.loadDevices();
    }

    @Override
    public void showDevicesEmpty() {
        mDevicesAdapter.setDevices(Collections.emptyList());
        mDevicesAdapter.notifyDataSetChanged();
    }

    public void showDevicesError() {
        DialogFactory.createGenericErrorDialog(getContext(), "Leider gab es ein problem beim Laden der Geräte");
    }
}
