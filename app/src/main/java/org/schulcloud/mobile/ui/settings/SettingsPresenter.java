package org.schulcloud.mobile.ui.settings;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.data.model.jsonApi.IncludedAttributes;
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

    private DataManager mDataManager;

    private Subscription mEventsSubscription;
    private Subscription mDevicesSubscription;

    private String[] mContributors;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewAttached(@NonNull SettingsMvpView view) {
        super.onViewAttached(view);
        mDataManager.isInDemoMode()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isInDemoMode -> {
                    sendToView(v -> v.showSupportsCalendar(!isInDemoMode));
                    sendToView(v -> v.showSupportsNotifications(!isInDemoMode));
                });
    }
    @Override
    protected void onViewDetached() {
        super.onViewDetached();
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
        mEventsSubscription = mDataManager.getEvents()
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
     * @param calendarContentUtil {CalendarContentUtil} - an instance of the CalendarContentUtil
     *                            for
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
            getViewOrThrow().showSyncToCalendarSuccessful();
    }

    /* Notifications */
    public void registerDevice() {
        if (mDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile",
                    android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")",
                    mDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            RxUtil.unsubscribe(mDevicesSubscription);
            mDevicesSubscription = mDataManager.createDevice(deviceRequest, token)
                    .subscribe(
                            deviceResponse -> {},
                            throwable -> {},
                            () -> sendToView(SettingsMvpView::reloadDevices));
        }
    }
    public void loadDevices() {
        RxUtil.unsubscribe(mDevicesSubscription);
        mDevicesSubscription = mDataManager.getDevices()
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
        mDevicesSubscription = mDataManager.deleteDevice(device.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        voidResponse -> {},
                        throwable -> {},
                        () -> {
                            sendToView(SettingsMvpView::reloadDevices);
                            mDataManager.getPreferencesHelper()
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
    public void showGitHub(@NonNull Resources resources) {
        getViewOrThrow().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_openSource_website)));
    }

    public void loadContributors(@NonNull Resources resources) {
        mContributors = resources.getStringArray(R.array.settings_about_contributors);
        getViewOrThrow().showContributors(mContributors);
    }
    public String[] getContributors() {
        return mContributors;
    }

}
