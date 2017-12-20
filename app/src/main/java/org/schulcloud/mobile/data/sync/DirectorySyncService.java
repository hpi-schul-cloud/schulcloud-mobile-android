package org.schulcloud.mobile.data.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.util.AndroidComponentUtil;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class DirectorySyncService extends Service {

    @Inject
    FileDataManager mFileDataManager;
    private Subscription mSubscription;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DirectorySyncService.class);
    }

    public static boolean isRunning(Context context) {
        return AndroidComponentUtil.isServiceRunning(context, DirectorySyncService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SchulCloudApplication.get(this).getComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Timber.i("Starting directory sync...");

        if (!NetworkUtil.isNetworkConnected(this)) {
            Timber.i("Sync canceled, connection not available");
            AndroidComponentUtil.toggleComponent(this, SyncOnConnectionAvailable.class, true);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

<<<<<<< 7d98e070801387c77a122fd23a30f82e95e74393
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.syncDirectories(mDataManager.getCurrentStorageContext())
=======
        if (mSubscription != null && !mSubscription.isUnsubscribed()) mSubscription.unsubscribe();

        // generate correct storageContext/path

        // maybe refactor it when we also support course/class folders
        String path = mFileDataManager.getCurrentStorageContext();

        mSubscription = mFileDataManager.syncDirectories(path)
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Directory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.print(e.getMessage());
                    }

                    @Override
                    public void onNext(Directory directory) {

                    }
                });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mSubscription != null) mSubscription.unsubscribe();
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