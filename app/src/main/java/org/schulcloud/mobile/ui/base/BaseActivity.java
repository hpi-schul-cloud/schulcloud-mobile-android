package org.schulcloud.mobile.ui.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.feedback.FeedbackDialog;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Single;
import rx.SingleSubscriber;
import timber.log.Timber;

public abstract class BaseActivity<V extends MvpView, P extends BasePresenter<V>>
        extends AppCompatActivity implements MvpView {
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicInteger sNextId = new AtomicInteger(0);
    private static final SparseArray<ConfigPersistentComponent> sComponentsMap = new SparseArray<>();

    @Inject
    DataManager mDataManager;

    private ActivityComponent mActivityComponent;
    private int mActivityId;
    private List<SingleSubscriber<? super Intent>> mActivityRequests;
    private List<SingleSubscriber<? super Boolean[]>> mPermissionRequests;

    private P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        mActivityId = savedInstanceState != null
                ? savedInstanceState.getInt(KEY_ACTIVITY_ID)
                : sNextId.getAndIncrement();
        ConfigPersistentComponent configPersistentComponent;
        if (null == sComponentsMap.get(mActivityId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(SchulCloudApplication.get(this).getComponent())
                    .build();
            sComponentsMap.put(mActivityId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = sComponentsMap.get(mActivityId);
        }
        mActivityComponent = configPersistentComponent.activityComponent(new ActivityModule(this));
    }
    protected final void readArguments(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            onReadArguments(getIntent());
    }
    public void onReadArguments(Intent intent) {
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mPresenter != null)
            //noinspection unchecked
            mPresenter.attachView((V) this);
    }
    @Override
    protected void onPause() {
        if (mPresenter != null)
            mPresenter.detachView();

        super.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVITY_ID, mActivityId);
    }
    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            if (mPresenter != null)
                mPresenter.destroy();

            Timber.i("Clearing ConfigPersistentComponent id=%d", mActivityId);
            sComponentsMap.remove(mActivityId);
        }
        super.onDestroy();
    }

    @Override
    @CallSuper
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.base_action_feedback:
                FeedbackDialog.newInstance(getClass().getSimpleName())
                        .show(getSupportFragmentManager(), null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }
    protected void setPresenter(@NonNull P presenter) {
        mPresenter = presenter;
    }

    @NonNull
    public Single<Boolean[]> requestPermissions(@NonNull String... permissions) {
        if (permissions.length == 0)
            throw new IllegalArgumentException("permissions.length must be > 0");

        boolean allGranted = true;
        for (String p : permissions)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        if (allGranted) {
            Boolean[] results = new Boolean[permissions.length];
            Arrays.fill(results, true);
            return Single.just(results);
        }

        if (mPermissionRequests == null)
            mPermissionRequests = new LinkedList<>();
        return Single.create(subscriber -> {
            mPermissionRequests.add(subscriber);
            ActivityCompat.requestPermissions(this, permissions, mPermissionRequests.size() - 1);
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        // Request not from this class
        if (requestCode >= mPermissionRequests.size()) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        //The request was interrupted
        if (permissions.length == 0) {
            mPermissionRequests.get(requestCode).onError(new InterruptedException());
            return;
        }

        Boolean[] results = new Boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++)
            results[i] = grantResults[i] == PackageManager.PERMISSION_GRANTED;
        mPermissionRequests.get(requestCode).onSuccess(results);
    }
    @NonNull
    public Single<Boolean[]> permissionsDeniedToError(
            @NonNull Single<Boolean[]> requestPermissionResults) {
        return requestPermissionResults
                .map(results -> {
                    boolean allGranted = true;
                    for (boolean result : results)
                        if (!result) {
                            allGranted = false;
                            break;
                        }

                    if (!allGranted)
                        throw new PermissionDeniedException("At least one permission was denied");
                    return results;
                });
    }

    public Single<Intent> startActivityForResult(@NonNull Intent intent) {
        return startActivityForResult(intent, null);
    }
    public Single<Intent> startActivityForResult(@NonNull Intent intent, @Nullable Bundle options) {
        if (mActivityRequests == null)
            mActivityRequests = new LinkedList<>();
        return Single.create(subscriber -> {
            mActivityRequests.add(subscriber);
            startActivityForResult(intent, mActivityRequests.size() - 1, options);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Request not from this class
        if (requestCode >= mActivityRequests.size()) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == RESULT_OK)
            mActivityRequests.get(requestCode).onSuccess(data);
        else
            mActivityRequests.get(requestCode)
                    .onError(new Exception("Activity result is: " + resultCode));
    }

    public void restartService(@NonNull Intent service) {
        stopService(service);
        startService(service);
    }

    @Inject
    public void setDataManager(DataManager dataManager) {
        mDataManager = dataManager;
    }

    /***** MVP View methods implementation *****/
    @Override
    public void goToSignIn() {
        mDataManager.signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}
