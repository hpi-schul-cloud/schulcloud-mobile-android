package org.schulcloud.mobile.data.local;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.util.FormatUtil;
import org.schulcloud.mobile.util.Pair;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class HomeworkDatabaseHelper extends BaseDatabaseHelper {
    @Inject
    HomeworkDatabaseHelper(Provider<Realm> realmProvider) {
        super(realmProvider);
    }

    @NonNull
    public Observable<Homework> setHomework(@NonNull Collection<Homework> newHomework) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed())
                return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newHomework));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    @NonNull
    public Observable<List<Homework>> getHomework() {
        Realm realm = mRealmProvider.get();
        return realm.where(Homework.class).findAllAsync().asObservable()
                .filter(RealmResults::isLoaded)
                .map(realm::copyFromRealm);
    }

    public Homework getHomeworkForId(@NonNull String homeworkId) {
        Realm realm = mRealmProvider.get();
        return realm
                .copyFromRealm(realm.where(Homework.class).equalTo("_id", homeworkId).findFirst());
    }

    @NonNull
    public Pair<Integer, Date> getOpenHomeworks() {
        Realm realm = mRealmProvider.get();
        Collection<Homework> homeworks = realm.where(Homework.class).findAll();

        Date now = new Date();
        int amount = 0;
        Calendar calendar = new GregorianCalendar(9999, 12, 31, 23, 59);
        Date nullDate = FormatUtil.parseDate(FormatUtil.toApiString(calendar.getTime()));
        Date nextDueDate = nullDate;
        for (Homework homework : homeworks) {
            Date untilDate = FormatUtil.parseDate(homework.dueDate);
            if (untilDate != null && now.after(untilDate))
                continue;

            amount++;
            if (untilDate != null && untilDate.before(nextDueDate))
                nextDueDate = untilDate;
        }

        if (nextDueDate == null)
            throw new IllegalStateException();
        return new Pair<>(amount, nextDueDate != nullDate ? nextDueDate : null);
    }
}
