package org.schulcloud.mobile.ui.settings;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.sync.DirectorySyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.data.sync.FileSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.files.DirectoriesAdapter;
import org.schulcloud.mobile.ui.files.FileActivity;
import org.schulcloud.mobile.ui.files.FilesAdapter;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DialogFactory;
import org.schulcloud.mobile.util.PermissionsUtil;
import org.schulcloud.mobile.util.firebase.FirebaseIDService;

import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.files.EventActivity.EXTRA_TRIGGER_SYNC_FLAG";

    private static final Integer CALENDAR_PERMISSION_CALLBACK_ID = 42;

    @Inject
    SettingsPresenter mSettingsPresenter;

    @BindView(R.id.btn_add_calendar)
    BootstrapButton btn_add_calendar;


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


        mSettingsPresenter.attachView(this);
        mSettingsPresenter.checkSignedIn();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(EventSyncService.getStartIntent(this));
        }

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
    public void connectToCalendar() {
        // grant calendar permission, powered sdk version 23
        PermissionsUtil.checkPermissions(CALENDAR_PERMISSION_CALLBACK_ID, this, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        CalendarContentUtil calendarContentUtil = new CalendarContentUtil(this);

        Set<String> calendars = calendarContentUtil.getCalendars();
        CharSequence[] calendarValues = calendars.toArray(new CharSequence[calendars.size()]);

        // saves selected calendar index on dialog prompting
        final Integer[] chosenValueIndex = new Integer[1];

        DialogFactory.createSingleSelectDialog(
                this,
                calendarValues,
                R.string.choose_calendar)
                .setItems(calendarValues, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chosenValueIndex[0] = i;
                    }
                })
                .setPositiveButton(R.string.dialog_action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println(calendarValues[chosenValueIndex[0]]);

                        // todo: send all events to calendar (via presenter)
                    }
                })
                .show();
    }
}
