package org.schulcloud.mobile.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.util.LongSparseArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.SchulCloudApplication;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.injection.component.ConfigPersistentComponent;
import org.schulcloud.mobile.injection.component.DaggerConfigPersistentComponent;
import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.ui.main.MainActivity;

import timber.log.Timber;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout mDrawer;
    protected ListView drawerList;
    protected ActionBarDrawerToggle drawerToggle;

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    private static final LongSparseArray<ConfigPersistentComponent> sComponentsMap = new LongSparseArray<>();

    private ActivityComponent mActivityComponent;
    private long mActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drawer_layout);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ArrayList<DrawerListItem> drawerListItems = new ArrayList<DrawerListItem>();
        drawerListItems.add(new DrawerListItem(0,"AIR° DEVICES"));
        drawerListItems.add(new DrawerListItem(1,"A/C Device [1]"));
        drawerListItems.add(new DrawerListItem(1,"A/C Device [2]"));
        drawerListItems.add(new DrawerListItem(1,"A/C Device [3]"));
        drawerListItems.add(new DrawerListItem(0,"AIR° FEATURES"));
        drawerListItems.add(new DrawerListItem(2,"SLEEP MODE"));
        drawerListItems.add(new DrawerListItem(2,"TRACKING MODE"));
        drawerListItems.add(new DrawerListItem(2,"SETTINGS"));
        DrawerAdapter mDrawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_item, drawerListItems);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(mDrawerAdapter);

        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        mActivityId = savedInstanceState != null ?
                savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", mActivityId);
            sComponentsMap.remove(mActivityId);
        }
        super.onDestroy();
    }

    public ActivityComponent activityComponent() {
        return mActivityComponent;
    }
}
