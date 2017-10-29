package org.schulcloud.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.files.FileFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * MainActivity displays a BottomNavigationView and the Toolbar, the content is managed by fragments
 * which inherit from {@link MainFragment}.
 */
public final class MainActivity extends BaseActivity implements MainMvpView {
    private static final String TAG = MainActivity.class.getSimpleName();

    //    public static final int TAB_DASHBOARD = R.id.main_navigation_dashboard;
    public static final int TAB_COURSES = R.id.main_navigation_courses;
    public static final int TAB_HOMEWORK = R.id.main_navigation_homework;
    public static final int TAB_FILES = R.id.main_navigation_files;
    //    public static final int TAB_MATERIALS = R.id.main_navigation_materials;
    //    public static final int TAB_ADMINISTRATION = R.id.main_navigation_administration;

    @Inject
    MainPresenter mMainPresenter;

    @Inject
    DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        mMainPresenter.attachView(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mMainPresenter.initTabs(
                new MainFragment[]{
                        CourseFragment.getInstance(),
                        HomeworkFragment.getInstance(),
                        FileFragment.getInstance()});

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(
                item -> {
                    mMainPresenter.onTabSelected(getTabIndexById(item.getItemId()));
                    return true;
                });
    }
    private int getTabIndexById(@IdRes int tabId) {
        switch (tabId) {
            case TAB_COURSES:
                return 0;
            case TAB_HOMEWORK:
                return 1;
            case TAB_FILES:
                return 2;
            default:
                return 0;
        }
    }
    public void addFragment(@NonNull MainFragment parent, @NonNull MainFragment child) {
        mMainPresenter.addFragment(parent, child);
    }
    public void removeFragment(@NonNull MainFragment fragment) {
        mMainPresenter.removeFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.main_action_logout:
                goToSignIn();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        mMainPresenter.onBackPressed();
    }
    @Override
    public void showFragment(@NonNull MainFragment fragment, int oldTabIndex, int newTabIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (newTabIndex > oldTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        else if (newTabIndex < oldTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }
}
