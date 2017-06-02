package org.schulcloud.mobile.ui.base;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;

import java.util.concurrent.atomic.AtomicLong;

import timber.log.Timber;

public class BaseFragment extends Fragment {
    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> sComponentsMap = new LongSparseArray<>();

    private ActivityComponent mActivityComponent;
    private long mActivityId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivityId = savedInstanceState != null ?
                savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ConfigPersistentComponent configPersistentComponent;
        if (null == sComponentsMap.get(mActivityId)) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .applicationComponent(SchulCloudApplication.get(activity).getComponent())
                    .build();
            sComponentsMap.put(mActivityId, configPersistentComponent);
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", mActivityId);
            configPersistentComponent = sComponentsMap.get(mActivityId);
        }
        mActivityComponent = configPersistentComponent.activityComponent(new ActivityModule(activity));
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }
}
