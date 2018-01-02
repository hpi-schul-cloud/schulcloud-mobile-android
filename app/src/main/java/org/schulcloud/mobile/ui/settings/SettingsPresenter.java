package org.schulcloud.mobile.ui.settings;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.data.model.jsonApi.IncludedAttributes;
import org.schulcloud.mobile.data.model.requestBodies.AccountRequest;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DaysBetweenUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private final UserDataManager mUserDataManager;
    private final EventDataManager mEventDataManager;
    private final NotificationDataManager mNotificationDataManager;


    private Subscription mDemoModeSubscription;
    private Subscription mEventsSubscription;
    private Subscription mDevicesSubscription;
    private Subscription mAccountSubscription;

    private String[] mContributors;

    @Inject
    public SettingsPresenter(UserDataManager userDataManager, EventDataManager eventDataManager,
                             NotificationDataManager notificationDataManager) {
        mUserDataManager = userDataManager;
        mEventDataManager = eventDataManager;
        mNotificationDataManager = notificationDataManager;
    }

    @Override
    public void onViewAttached(@NonNull SettingsMvpView view) {
        super.onViewAttached(view);

        RxUtil.unsubscribe(mDemoModeSubscription);
        mDemoModeSubscription = mUserDataManager.isInDemoMode()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isInDemoMode -> {
                    sendToView(v -> v.showSupportsCalendar(!isInDemoMode));
                    sendToView(v -> v.showSupportsNotifications(!isInDemoMode));
                    sendToView(v -> v.showSupportsProfile(!isInDemoMode));
                });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mDemoModeSubscription);
        RxUtil.unsubscribe(mEventsSubscription);
        RxUtil.unsubscribe(mDevicesSubscription);
    }

    /* Calendar */
    /**
     * Fetches events from local db
     *
     * @param promptForCalendar {Boolean} - whether to open a calendar-choose-dialog
     */
    public void loadEvents(boolean promptForCalendar) {
        RxUtil.unsubscribe(mEventsSubscription);
        mEventsSubscription = mEventDataManager.getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        events -> {
                            if (events.isEmpty())
                                sendToView(SettingsMvpView::showEventsEmpty);
                            else
                                sendToView(view ->
                                        view.connectToCalendar(events, promptForCalendar));
                        },
                            throwable -> Timber.e(throwable, "There was an error loading the events."));
    }
    /**
     * Syncs given events to local calendar
     *
     * @param calendarName        {String} - the calendar in which the events will be inserted
     * @param events              {List<Event>} - the events which will be inserted into the
     *                            calendar
     * @param calendarContentUtil {CalendarContentUtil} - an instance of the CalendarContentUtil for
     *                            handling the local calendar storage
     * @param showToast           {Boolean} - whether to show a toast after writing
     */
    public void writeEventsToLocalCalendar(@NonNull String calendarName,
            @NonNull List<Event> events,
            @NonNull CalendarContentUtil calendarContentUtil, boolean showToast) {
        for (Event event : events) {
            // syncing by deleting first and writing again
            calendarContentUtil.deleteEventByUid(event._id);

            // handle recurrent events
            String recurringRule = null;
            try {
                Included includedInformation = event.included.get(0);
                if (includedInformation.getType().equals(CalendarContentUtil.RECURRENT_TYPE)) {
                    StringBuilder builder = new StringBuilder();

                    // count days/weeks from startDate to untilDate
                    Date startDate = new Date(Long.parseLong(event.start));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date untilDate = dateFormat
                            .parse(includedInformation.getAttributes().getUntil());
                    Integer betweenDates = 0;
                    switch (includedInformation.getAttributes().getFreq()) {
                        case IncludedAttributes.FREQ_WEEKLY:
                            betweenDates = DaysBetweenUtil.weeksBetween(startDate, untilDate);
                            break;
                        case IncludedAttributes.FREQ_DAILY:
                            betweenDates = DaysBetweenUtil.daysBetween(startDate, untilDate);
                            break;
                        default:
                            break;
                    }

                    builder
                            .append("FREQ=")
                            .append(includedInformation.getAttributes().getFreq())
                            .append(";")
                            .append("COUNT=")
                            .append(betweenDates)
                            .append(";")
                            .append("WKST=")
                            .append(includedInformation.getAttributes().getWkst());
                    recurringRule = builder.toString();
                }
            } catch (Exception e) {
                // do nothing when its not a recurrent event
            }

            calendarContentUtil.createEvent(calendarName, event, recurringRule);
        }

        if (showToast)
            sendToView(SettingsMvpView::showSyncToCalendarSuccessful);
    }

    /* Notifications */
    public void registerDevice() {
        if (mNotificationDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile",
                    android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")",
                    mUserDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            RxUtil.unsubscribe(mEventsSubscription);
            mEventsSubscription = mNotificationDataManager.createDevice(deviceRequest, token)
                    .subscribe(
                            deviceResponse -> {},
                            throwable -> {},
                            () -> sendToView(SettingsMvpView::reloadDevices));
        }
    }
    public void loadDevices() {
        RxUtil.unsubscribe(mDevicesSubscription);
        mDevicesSubscription = mNotificationDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        devices -> {
                            if (devices.isEmpty()) {
                                // TODO: Show something
                            } else
                                sendToView(view -> view.showDevices(devices));
                        },
                        //TODO: Show error
                        throwable -> Timber.e(throwable, "There was an error loading the users."));
    }
    public void deleteDevice(@NonNull Device device) {
        RxUtil.unsubscribe(mDevicesSubscription);
        mDevicesSubscription = mNotificationDataManager.deleteDevice(device.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        voidResponse -> {},
                        throwable -> {},
                        () -> {
                            sendToView(SettingsMvpView::reloadDevices);
                            mNotificationDataManager.getPreferencesHelper()
                                    .clear(PreferencesHelper.PREFERENCE_MESSAGING_TOKEN);
                        });
    }

    /* About */
    public void contact(@NonNull String to, @NonNull String subject) {
        getViewOrThrow().showExternalContent(Uri.parse("mailto:" + to + "?subject=" + subject));
    }
    public void showImprint(@NonNull Resources resources) {
        getViewOrThrow().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_imprint_website)));
    }
    public void showPrivacyPolicy(@NonNull Resources resources) {
        getViewOrThrow().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_privacyPolicy_website)));
    }
    public void showGitHub(@NonNull Resources resources) {
        getViewOrThrow().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_openSource_website)));
    }

    public void loadContributors(@NonNull Resources resources) {
        mContributors = resources.getStringArray(R.array.settings_about_contributors);
        sendToView(v -> v.showContributors(mContributors));
    }
    public String[] getContributors() {
        return mContributors;
    }

    /* Profile */
    public void loadProfile()
    {
        mAccountSubscription = mDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        currentUser -> {
                            sendToView(v -> v.showProfile(currentUser));
                        },
                        throwable -> {
                            Timber.e(throwable, "An error occured while loading the profile"
                            );
                            sendToView(v -> v.showProfileError());
                        });
    }

    public void changeProfile(@NonNull String firstName, @NonNull String lastName,
                              @NonNull String email, @NonNull String gender, @Nullable String password,
                              @Nullable String newPassword, @Nullable String newPasswordRepeat)
    {
        //TODO:include password
        AccountRequest accountRequest = new AccountRequest(mDataManager.getCurrentUserId(),firstName,
                email,lastName,mDataManager.getCurrentSchoolID(),"",gender);
        mAccountSubscription = mDataManager.changeAccountInfo(accountRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accountResponse -> {},
                        throwable -> Log.e("Accounts","OnError",throwable),
                        () -> sendToView(v -> v.showProfileChanged()));
    }

}
