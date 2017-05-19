package org.schulcloud.mobile.ui.settings;

import android.util.Log;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.jsonApi.Included;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.CalendarContentUtil;
import org.schulcloud.mobile.util.DaysBetweenUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SettingsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadEvents() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Event>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the events.");
                    }

                    @Override
                    public void onNext(List<Event> events) {
                        if (events.isEmpty()) {
                            getMvpView().showEventsEmpty();
                        } else {
                            getMvpView().connectToCalendar(events);
                        }
                    }
                });
    }

    public void writeEventsToLocalCalendar(Integer calendarId, List<Event> events, CalendarContentUtil calendarContentUtil) {
        for (Event event : events) {
            // syncing by deleting first and writing again
            calendarContentUtil.deleteEventByUid(event._id);

            String recurringRule = null;

            // handle recurrent events
            try {
                Included includedInformation = event.included.get(0);
                if (includedInformation.getType().equals(CalendarContentUtil.RECURRENT_TYPE)) {
                    StringBuilder builder = new StringBuilder();

                    // count days/weeks from startDate to untilDate
                    Date startDate = new Date(Long.parseLong(event.start));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date untilDate = dateFormat.parse(includedInformation.getAttributes().getUntil());
                    Integer betweenDates = 0;
                    switch(includedInformation.getAttributes().getFreq()) {
                        case "WEEKLY": betweenDates = DaysBetweenUtil.weeksBetween(startDate, untilDate); break;
                        case "DAILY": betweenDates = DaysBetweenUtil.daysBetween(startDate, untilDate); break;
                        default: break;
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
            } catch (Exception e ) {
                // do nothing when its not a recurrent event
            }

            calendarContentUtil.createEvent(calendarId, event, recurringRule);
        }

    }


    public void checkSignedIn() {
        super.isAlreadySignedIn(mDataManager);
    }

}
