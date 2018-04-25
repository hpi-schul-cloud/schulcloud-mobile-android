package org.schulcloud.mobile.data.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.util.AndroidComponentUtil;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SubmissionSyncService extends Service {

    @Inject
    SubmissionDataManager mSubmissionDataManager;
    private Subscription mSubscription;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SubmissionSyncService.class);
    }

    public static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, SubmissionSyncService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SchulCloudApplication.get(this).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Timber.i("Starting submission sync...");

        if (!NetworkUtil.isNetworkConnected(this)) {
            Timber.i("Sync canceled, connection not available");
            AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        RxUtil.unsubscribe(mSubscription);
        mSubscription = mSubmissionDataManager.syncSubmissions()
                .subscribeOn(Schedulers.io())
                .subscribe(submission -> {},
                        throwable -> {
                            Timber.w(throwable, "Error syncing.");
                            stopSelf(startId);
                        }, () -> {
                            Timber.i("Synced successfully!");
                            stopSelf(startId);
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