package org.schulcloud.mobile.ui.start;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.settings.DevicesAdapter;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.PermissionsUtil;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends BaseActivity implements StartMvpView {


    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FileActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        this.goToMain();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void goToMain() {
        Handler handler = new Handler();
        Context context = this;
        handler.postDelayed(() -> {
            Intent intent = new Intent(context, FileActivity.class);
            context.startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    public void goToSignIn() {

    }


}
