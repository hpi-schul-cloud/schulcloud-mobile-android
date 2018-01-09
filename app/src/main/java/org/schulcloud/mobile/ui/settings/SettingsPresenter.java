package org.schulcloud.mobile.ui.settings;

import android.content.res.Resources;
import android.database.Observable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.data.DataBufferObserver;
import com.google.firebase.iid.FirebaseInstanceId;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.data.model.jsonApi.IncludedAttributes;
import org.schulcloud.mobile.data.model.requestBodies.AccountRequest;
import org.schulcloud.mobile.data.model.requestBodies.DeviceRequest;
import org.schulcloud.mobile.data.model.requestBodies.UserRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DaysBetweenUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.BooleanSupplier;

import javax.inject.Inject;

import rx.Completable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import timber.log.Timber;

@ConfigPersistent
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private Subscription eventSubscription;
    private Subscription mProfileSubscription;

    private String[] mContributors;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
        mDataManager.isInDemoMode()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isInDemoMode -> {
                    getMvpView().showSupportsCalendar(!isInDemoMode);
                    getMvpView().showSupportsNotifications(!isInDemoMode);
                    getMvpView().showSupportsProfile(!isInDemoMode);
                });
    }
    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
        RxUtil.unsubscribe(eventSubscription);
    }

    // Calender
    /**
     * Fetches events from local db
     *
     * @param promptForCalendar {Boolean} - whether to open a calendar-choose-dialog
     */
    public void loadEvents(boolean promptForCalendar) {
        checkViewAttached();
        RxUtil.unsubscribe(eventSubscription);
        eventSubscription = mDataManager.getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        events -> {
                            if (events.isEmpty())
                                getMvpView().showEventsEmpty();
                            else
                                getMvpView().connectToCalendar(events, promptForCalendar);
                        },
                        throwable -> Timber.e(throwable, "There was an error loading the events."));
    }
    /**
     * Syncs given Events to local calendar
     *
     * @param calendarName        {String} - the calendar in which the events will be inserted
     * @param events              {List<Event>} - the events which will be inserted into the
     *                            calendar
     * @param calendarContentUtil {CalendarContentUtil} - an instance of the CalendarContentUtil for
     *                            handling the local calendar storage
     * @param showToast           {Boolean} - whether to show a toast after writing
     */
    public void writeEventsToLocalCalendar(String calendarName, List<Event> events,
                                           CalendarContentUtil calendarContentUtil, Boolean showToast) {
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
            getMvpView().showSyncToCalendarSuccessful();
    }

    // Notifications
    public void registerDevice() {
        if (mDataManager.getPreferencesHelper().getMessagingToken().equals("null")) {
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d("FirebaseID", "Refreshed token: " + token);

            Log.d("FirebaseID", "sending registration to Server");
            DeviceRequest deviceRequest = new DeviceRequest("firebase", "mobile",
                    android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")",
                    mDataManager.getCurrentUserId(), token, android.os.Build.VERSION.INCREMENTAL);

            RxUtil.unsubscribe(mSubscription);
            mSubscription = mDataManager.createDevice(deviceRequest, token)
                    .subscribe(
                            deviceResponse -> {
                            },
                            throwable -> {
                            },
                            () -> getMvpView().reloadDevices());
        }
    }
    public void loadDevices() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getDevices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        devices -> {
                            if (devices.isEmpty()) {
                                // TODO: Show something
                            } else {
                                getMvpView().showDevices(devices);
                            }
                        },
                        //TODO: Show error
                        throwable -> Timber.e(throwable, "There was an error loading the users."));
    }
    public void deleteDevice(Device device) {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.deleteDevice(device.token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        voidResponse -> {
                        },
                        throwable -> {
                        },
                        () -> {
                            getMvpView().reloadDevices();
                            mDataManager.getPreferencesHelper()
                                    .clear(PreferencesHelper.PREFERENCE_MESSAGING_TOKEN);
                        });
    }

    // About
    public void contact(@NonNull String to, @NonNull String subject) {
        getMvpView().showExternalContent(Uri.parse("mailto:" + to + "?subject=" + subject));
    }
    public void showImprint(@NonNull Resources resources) {
        getMvpView().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_imprint_website)));
    }
    public void showGitHub(@NonNull Resources resources) {
        getMvpView().showExternalContent(
                Uri.parse(resources.getString(R.string.settings_about_openSource_website)));
    }

    public void loadContributors(@NonNull Resources resources) {
        mContributors = resources.getStringArray(R.array.settings_about_contributors);
        getMvpView().showContributors(mContributors);
    }
    public String[] getContributors() {
        return mContributors;
    }

    /* Profile */
    public void loadProfile()
    {
        mProfileSubscription = mDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        currentUser -> {
                            getMvpView().showProfile(currentUser);
                        },
                        throwable -> {
                            Timber.e(throwable, "An error occured while loading the profile"
                            );
                            getMvpView().showProfileError();
                        });
    }

    public void changeProfile(@NonNull String firstName, @NonNull String lastName,
                              @NonNull String email, @NonNull String gender, @Nullable String password,
                              @Nullable String newPassword, @Nullable String newPasswordRepeat)
    {
        String currentPassword = password;
        if(newPassword != "" && newPassword != password && newPassword == newPasswordRepeat)
            currentPassword = newPassword;

        CurrentUser currentUser = mDataManager.getCurrentUser().toBlocking().value();
        String displayName = currentUser.displayName;
        String _id = currentUser._id;
        String schoolID = currentUser.schoolId;

        AccountRequest accountRequest = new AccountRequest(displayName,currentPassword);
        UserRequest userRequest = new UserRequest(_id,firstName,lastName,email,schoolID,gender);

        mDataManager.signIn(currentUser.displayName,currentPassword).doOnError(throwable -> {
            getMvpView().showPasswordChangeFailed();
        });

        mProfileSubscription = mDataManager.changeProfileInfo(accountRequest,userRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userResponse -> {},
                        throwable -> Log.e("Profile","OnError",throwable),
                        () -> getMvpView().showProfileChanged()
                );
    }

}
