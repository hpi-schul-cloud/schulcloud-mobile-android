package org.schulcloud.mobile.ui.settings;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.sync.DeviceSyncService;
import org.schulcloud.mobile.data.sync.EventSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.PermissionsUtil;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity<SettingsMvpView, SettingsPresenter>
        implements SettingsMvpView {

    public static final Integer CALENDAR_PERMISSION_CALLBACK_ID = 42;
    private static final String EXTRA_TRIGGER_SYNC = "org.schulcloud.mobile.ui.settings.SettingsActivity.EXTRA_TRIGGER_SYNC";

    private CurrentUser mCurrentUser;
    private ArrayAdapter<CharSequence> spinner_adapter;

    @Inject
    SettingsPresenter mSettingsPresenter;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    DevicesAdapter mDevicesAdapter;

    // Calendar
    @BindView(R.id.calendar)
    LinearLayout calendar;
    @BindView(R.id.switch_calendar)
    Switch switch_calendar;

    // Notifications
    @BindView(R.id.notifications)
    LinearLayout notifications;
    @BindView(R.id.btn_create_device)
    BootstrapButton btn_create_device;
    @BindView(R.id.devices_recycler_view)
    RecyclerView devices_recycler_view;
    //Profile
    @BindView(R.id.settings_add_if_not_in_demo_mode)
    LinearLayout addIfNotDemoMode;
    @BindView(R.id.settings_gender_spinner)
    Spinner gender_spinner;
    @BindView(R.id.settings_name_EditText)
    EditText name_editText;
    @BindView(R.id.settings_last_name_EditText)
    EditText lastName_editText;
    @BindView(R.id.settings_email_EditText)
    EditText email_EditText;
    @BindView(R.id.settings_password_editText)
    EditText password_editText;
    @BindView(R.id.settings_newPassword_editText)
    EditText newPassword_editText;
    @BindView(R.id.settings_newPasswordRepeat_editText)
    EditText newPasswordRepeat_editText;
    @BindView(R.id.settings_submit)
    Button settings_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mSettingsPresenter);
        readArguments(savedInstanceState);

        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.settings_title);

        // Calender
        updateCalendarSwitch(
                mPreferencesHelper.getCalendarSyncEnabled(),
                mPreferencesHelper.getCalendarSyncName());
        switch_calendar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mSettingsPresenter.loadEvents(true);
            mPreferencesHelper.saveCalendarSyncEnabled(isChecked);
            updateCalendarSwitch(isChecked, mPreferencesHelper.getCalendarSyncName());
        });

        // Notifications
        btn_create_device.setOnClickListener(view -> mSettingsPresenter.registerDevice());

        devices_recycler_view.setAdapter(mDevicesAdapter);
        devices_recycler_view.setLayoutManager(new LinearLayoutManager(this));

        // About
        findViewById(R.id.settings_about_contributors).setOnLongClickListener(v -> {
            ClipboardManager clipboardManager =
                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(
                    getString(R.string.settings_about_contributors_clipBoard_title),
                    TextUtils.join(getString(R.string.general_list_separator),
                            mSettingsPresenter.getContributors()));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, R.string.settings_about_contributors_clipBoard_notification,
                    Toast.LENGTH_SHORT).show();
            return true;
        });
        findViewById(R.id.about_github).setOnClickListener(v ->
                mSettingsPresenter.showGitHub(getResources()));
        findViewById(R.id.about_contact).setOnClickListener(v ->
                mSettingsPresenter.contact(
                        getString(R.string.settings_about_contact_mail_to),
                        getString(R.string.settings_about_contact_mail_subject)));
        findViewById(R.id.about_imprint).setOnClickListener(v ->
                mSettingsPresenter.showImprint(getResources()));
        findViewById(R.id.about_privacyPolicy).setOnClickListener(v ->
                mSettingsPresenter.showPrivacyPolicy(getResources()));

        // Profile
        settings_submit.setOnClickListener(listener -> {
            String name = name_editText.getText().toString() != null ?
                    name_editText.getText().toString() : mCurrentUser.firstName;
            String last_name = lastName_editText.getText().toString() != null ?
                    lastName_editText.getText().toString() : mCurrentUser.lastName;
            String email = email_EditText.getText().toString() != null?
                    email_EditText.getText().toString() : mCurrentUser.email;
            String gender = ArrayAdapter.createFromResource(this, R.array.genderArrayPosReference,
                    R.layout.item_gender_spinner)
                    .getItem(gender_spinner.getSelectedItemPosition()).toString();
            if(gender.equals("Choose Gender"))
                gender = null;
            String password = password_editText.getText().toString();
            String newPassword = newPassword_editText.getText().toString();
            String newPasswordRepeat = newPasswordRepeat_editText.getText().toString();
            mSettingsPresenter.changeProfile(name,last_name,email,gender,password,newPassword,
                    newPasswordRepeat);
        });
        newPassword_editText.setHint(R.string.settings_newPasswordHint);
        newPasswordRepeat_editText.setHint(R.string.settings_newPasswordRepeatHint);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.genderArray,
                R.layout.item_gender_spinner);
        spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gender_spinner.setAdapter(spinner_adapter);

        // Profile
        settings_submit.setOnClickListener(listener -> {
            String name = name_editText.getText().toString() != null ?
                    name_editText.getText().toString() : mCurrentUser.firstName;
            String last_name = lastName_editText.getText().toString() != null ?
                    lastName_editText.getText().toString() : mCurrentUser.lastName;
            String email = email_EditText.getText().toString() != null?
                    email_EditText.getText().toString() : mCurrentUser.email;
            String gender = gender_spinner.getSelectedItem().toString();
            String password = password_editText.getText().toString();
            String newPassword = newPassword_editText.getText().toString();
            String newPasswordRepeat = newPasswordRepeat_editText.getText().toString();
            mSettingsPresenter.changeProfile(name,last_name,email,gender,password,newPassword,
                    newPasswordRepeat);
        });
        newPassword_editText.setHint(R.string.settings_newPasswordHint);
        newPasswordRepeat_editText.setHint(R.string.settings_newPasswordRepeatHint);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.genderArray,
                R.layout.item_gender_spinner);
        spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gender_spinner.setAdapter(spinner_adapter);

        // Presenter
        if (mPreferencesHelper.getCalendarSyncEnabled())
            mSettingsPresenter.loadEvents(false);
        mSettingsPresenter.loadDevices();
        mSettingsPresenter.loadContributors(getResources());
        mSettingsPresenter.loadProfile();
    }
    @Override
    public void onReadArguments(Intent intent) {
        if (intent.getBooleanExtra(EXTRA_TRIGGER_SYNC, true)) {
            startService(EventSyncService.getStartIntent(this));
            startService(DeviceSyncService.getStartIntent(this));
        }
    }
    private void updateCalendarSwitch(boolean isChecked, @NonNull String calendarName) {
        switch_calendar.setChecked(isChecked);
        if (isChecked && calendarName != null)
            switch_calendar.setText(
                    getString(R.string.settings_calendar_sync_calenderChosen, calendarName));
        else
            switch_calendar.setText(R.string.settings_calendar_sync);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /***** MVP View methods implementation *****/
    // Calender
    @Override
    public void showSupportsCalendar(boolean supportsCalendar) {
        ViewUtil.setVisibility(calendar, supportsCalendar);
    }
    @Override
    public void showEventsEmpty() {
        //Toast.makeText(this, R.string.empty_events, Toast.LENGTH_LONG).show();
    }

    @Override
    public void connectToCalendar(@NonNull List<Event> events, boolean promptForCalendar) {
        // grant calendar permission, powered sdk version 23
        PermissionsUtil.checkPermissions(
                CALENDAR_PERMISSION_CALLBACK_ID,
                this,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR);
        CalendarContentUtil calendarContentUtil = new CalendarContentUtil(this);

        Set<String> calendars = calendarContentUtil.getCalendars();
        CharSequence[] calendarValues = calendars.toArray(new CharSequence[calendars.size()]);

        // saves selected calendar index on dialog prompting
        final Integer[] chosenValueIndex = new Integer[1];

        if (promptForCalendar) {
            DialogFactory.createSingleSelectDialog(
                    this,
                    calendarValues,
                    Arrays.asList(calendarValues).indexOf(mPreferencesHelper.getCalendarSyncName()),
                    (dialogInterface, i) -> chosenValueIndex[0] = i,
                    R.string.settings_calendar_choose)
                    .setPositiveButton(R.string.dialog_action_ok,
                            (dialogInterface, i) -> { // handle choice
                                String calendarName = calendarValues[chosenValueIndex[0]]
                                        .toString();
                                Log.i("[CALENDAR CHOSEN]: ", calendarName);
                                mPreferencesHelper.saveCalendarSyncName(calendarName);
                                updateCalendarSwitch(true, calendarName);

                                // send all events to calendar
                                mSettingsPresenter.writeEventsToLocalCalendar(
                                        calendarName,
                                        events,
                                        calendarContentUtil,
                                        promptForCalendar);
                            })
                    .show();
        } else
            mSettingsPresenter.writeEventsToLocalCalendar(
                    mPreferencesHelper.getCalendarSyncName(),
                    events,
                    calendarContentUtil,
                    promptForCalendar);
    }

    @Override
    public void showSyncToCalendarSuccessful() {
        DialogFactory.createSuperToast(
                this,
                getResources().getString(R.string.settings_calendar_sync_successful),
                PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .show();
    }

    // Notifications
    @Override
    public void showSupportsNotifications(boolean supportsNotifications) {
        ViewUtil.setVisibility(notifications, supportsNotifications);
    }
    @Override
    public void showDevices(@NonNull List<Device> devices) {
        mDevicesAdapter.setDevices(devices);
    }

    @Override
    public void reloadDevices() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        finish();
    }

    // About
    @Override
    public void showContributors(@NonNull String[] contributors) {
        LinearLayout contributorsWrapper = ButterKnife.findById(this,
                R.id.settings_about_contributors);

        int[] attrs = new int[]{android.R.attr.textColor};
        TypedArray a = obtainStyledAttributes(R.style.Preference_Description, attrs);
        int textColor = a.getColor(0, 0);
        a.recycle();

        for (String contributor : contributors) {
            TextView textView = new TextView(this, null, R.style.Preference_Description);
            textView.setPadding(
                    getResources().getDimensionPixelSize(R.dimen.bootstrap_alert_paddings),
                    0, 0, 0);
            textView.setTextColor(textColor);
            textView.setText(contributor);
            contributorsWrapper.addView(textView);
        }
    }
    @Override
    public void showExternalContent(@NonNull Uri data) {
        startActivity(new Intent(Intent.ACTION_VIEW, data));
    }

    //Profile
    @Override
    public void showProfile(CurrentUser user) {
        mCurrentUser = user;
        name_editText.setText(mCurrentUser.firstName);
        lastName_editText.setText(mCurrentUser.lastName);
        email_EditText.setText(mCurrentUser.email);
        List<String> genderReferenceArray =
                Arrays.asList(getResources().getStringArray(R.array.genderArrayPosReference));
        gender_spinner.setSelection(genderReferenceArray.indexOf(mCurrentUser.gender));
    }

    @Override
    public void showProfileError()
    {
        DialogFactory.createGenericErrorDialog(this, R.string.settings_profile_loading_error).show();
    }

    @Override
    public void showProfileChanged()
    {
        reloadProfile();
        Toast.makeText(this,R.string.settings_profile_changing_error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reloadProfile() {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
        finish();
    }

    @Override
    public void showSupportsProfile(boolean supportsProfile) {
        ViewUtil.setVisibility(addIfNotDemoMode,supportsProfile);
    }

    @Override
    public void showPasswordChangeFailed(){
        DialogFactory.createGenericErrorDialog(this,R.string.settings_showPasswordChangeFailed).show();
    }

    @Override
    public void showPasswordBad(){
        DialogFactory.createGenericErrorDialog(this,"Das Passwort muss mindestens 8 Zeichen lang sein und" +
                " große und kleine Buchstaben sowie Zahlen beinhalten!");
    }
}
