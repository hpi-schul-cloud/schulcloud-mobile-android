package org.schulcloud.mobile.data.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.util.AndroidComponentUtil;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TopicSyncService extends Service {
    private static final String EXTRA_COURSE_ID = "org.schulcloud.mobile.data.sync.TopicSyncService.EXTRA_COURSE_ID";

    @Inject
    DataManager mDataManager;
    private Subscription mSubscription;

    private String courseId = null;

    public static Intent getStartIntent(@NonNull Context context) {
        return getStartIntent(context, null);
    }
    public static Intent getStartIntent(@NonNull Context context, @Nullable String courseId) {
        return new Intent(context, TopicSyncService.class)
                .putExtra(EXTRA_COURSE_ID, courseId);
    }

    public static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, TopicSyncService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SchulCloudApplication.get(this).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Timber.i("Starting topic sync...");

        if (!NetworkUtil.isNetworkConnected(this)) {
            Timber.i("Sync canceled, connection not available");
            AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            courseId = extras.getString(EXTRA_COURSE_ID);
            //The key argument here must match that used in the other activity
        }

        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.syncTopics(courseId)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Topic>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("Synced successfully!");
                        stopSelf(startId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.w(e, "Error syncing.");
                        stopSelf(startId);
                    }

                    @Override
                    public void onNext(Topic topic) {
                    }
                });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        RxUtil.unsubscribe(mSubscription);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static class SyncOnConnectionAvailable extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
                    && NetworkUtil.isNetworkConnected(context)) {
                Timber.i("Connection is now available, triggering sync...");
                AndroidComponentUtil.toggleComponent(context, this.getClass(), false);
                context.startService(getStartIntent(context));
            }
        }

    }
}