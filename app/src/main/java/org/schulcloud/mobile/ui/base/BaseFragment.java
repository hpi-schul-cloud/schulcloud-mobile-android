package org.schulcloud.mobile.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import java.util.concurrent.atomic.AtomicInteger;

import rx.Single;
import timber.log.Timber;

public abstract class BaseFragment<V extends MvpView, P extends BasePresenter<V>> extends Fragment
        implements MvpView {
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicInteger sNextId = new AtomicInteger(0);
    private static final SparseArray<ConfigPersistentComponent> sComponents = new SparseArray<>();

    private ActivityComponent mActivityComponent;
    private int mActivityId;

    private P mPresenter;

    public BaseFragment() {
        mActivityId = sNextId.getAndIncrement();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            mActivityId = savedInstanceState.getInt(KEY_ACTIVITY_ID);

        ConfigPersistentComponent configPersistentComponent = sComponents.get(mActivityId);
        if (configPersistentComponent == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(SchulCloudApplication.get(getActivity()).getComponent())
                    .build();
            sComponents.put(mActivityId, configPersistentComponent);
        } else
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
        mActivityComponent = configPersistentComponent
                .activityComponent(new ActivityModule((BaseActivity) getActivity()));
    }
    protected final void readArguments(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            onReadArguments(getArguments());
    }
    public void onReadArguments(Bundle args) {
    }
    @Override
    public void onResume() {
        super.onResume();

        if (mPresenter != null)
            //noinspection unchecked
            mPresenter.attachView((V) this);
    }
    @Override
    public void onPause() {
        if (mPresenter != null)
            mPresenter.detachView();

        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVITY_ID, mActivityId);
    }

    @NonNull
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
    @NonNull
    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }
    public int getActivityId() {
        return mActivityId;
    }
    protected void setPresenter(@NonNull P presenter) {
        mPresenter = presenter;
    }

    @NonNull
    public Single<Boolean[]> requestPermissions(@NonNull String... permissions) {
        return getBaseActivity().requestPermissions(permissions);
    }
    @NonNull
    public Single<Boolean[]> permissionsDeniedToError(
            @NonNull Single<Boolean[]> requestPermissionResults) {
        return getBaseActivity().permissionsDeniedToError(requestPermissionResults);
    }

    @NonNull
    public Single<Intent> startActivityForResult(@NonNull Intent intent) {
        return getBaseActivity().startActivityForResult(intent);
    }
    @NonNull
    public Single<Intent> startActivityForResult(@NonNull Intent intent, @Nullable Bundle options) {
        return getBaseActivity().startActivityForResult(intent, options);
    }

    public void startService(@NonNull Intent service) {
        getActivity().startService(service);
    }
    public void stopService(@NonNull Intent service) {
        getActivity().stopService(service);
    }
    public void restartService(@NonNull Intent service) {
        getBaseActivity().restartService(service);
    }

    @Override
    public void goToSignIn() {
        startActivity(new Intent(getContext(), SignInActivity.class));
    }
}
