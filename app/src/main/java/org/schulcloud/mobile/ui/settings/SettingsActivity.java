package org.schulcloud.mobile.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.EventActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    SettingsPresenter mSettingsPresenter;

    @Inject
    DevicesAdapter mDevicesAdapter;

    @BindView(R.id.btn_add_calendar)
    Button btn_add_calendar;

    @BindView(R.id.btn_create_device)
    BootstrapButton btn_create_device;

    @BindView(R.id.devices_recycler_view)
    RecyclerView devices_recycler_view;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_settings, null, false);
        mDrawer.addView(contentView, 0);
        ButterKnife.bind(this);


        devices_recycler_view.setAdapter(mDevicesAdapter);
        devices_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mSettingsPresenter.attachView(this);
        mSettingsPresenter.checkSignedIn();

        mSettingsPresenter.loadDevices();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(EventSyncService.getStartIntent(this));
            startService(DeviceSyncService.getStartIntent(this));
        }

        btn_create_device.setOnClickListener(view -> mSettingsPresenter.registerDevice());
        btn_add_calendar.setOnClickListener(view -> mSettingsPresenter.addEventsToLocalCalendar());
    }

    @Override
    protected void onDestroy() {
        mSettingsPresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/


    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void showEventsEmpty() {
        Toast.makeText(this, R.string.empty_events, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDevices(List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
        mDevicesAdapter.notifyDataSetChanged();
    }
}
