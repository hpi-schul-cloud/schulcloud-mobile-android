package org.schulcloud.mobile.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardFragment;
import org.schulcloud.mobile.ui.files.overview.FileOverviewFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.news.NewsFragment;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.WebUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * MainActivity displays a BottomNavigationView and the Toolbar, the content is managed by fragments
 * which inherit from {@link MainFragment}.
 */
public final class MainActivity
        extends BaseActivity<MainMvpView<MainFragment>, MainPresenter<MainFragment>>
        implements MainMvpView<MainFragment> {
    private static final int TAB_DASHBOARD = R.id.main_navigation_dashboard;
    private static final int TAB_NEWS = R.id.main_navigation_news;
    private static final int TAB_COURSES = R.id.main_navigation_courses;
    private static final int TAB_HOMEWORK = R.id.main_navigation_homework;
    private static final int TAB_FILES = R.id.main_navigation_files;
    //    private static final int TAB_MATERIALS = R.id.main_navigation_materials;
    //    private static final int TAB_ADMINISTRATION = R.id.main_navigation_administration;

    @Inject
    MainPresenter<MainFragment> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mPresenter);
        mPresenter.checkSignedIn(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (!NetworkUtil.isNetworkConnected(this)) {
            TextView offline = (TextView) findViewById(R.id.offlineBadge);
            offline.setVisibility(View.VISIBLE);
        }

        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(
                item -> {
                    mPresenter.onTabSelected(getTabIndexById(item.getItemId()));
                    return true;
                });

        mPresenter.setStartUrl(getIntent().getData());
    }
    private int getTabIndexById(@IdRes int tabId) {
        switch (tabId) {
            case TAB_DASHBOARD:
                return 0;
            case TAB_NEWS:
                return 1;
            case TAB_COURSES:
                return 2;
            case TAB_HOMEWORK:
                return 3;
            case TAB_FILES:
                return 4;
            default:
                return 0;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null)
            WebUtil.openUrl(this, intent.getData());
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

    @NonNull
    @Override
    protected String provideDetailedFeedbackContext() {
        int currentViewId = mPresenter.getCurrentViewId();
        return currentViewId + ", " + findFragment(currentViewId).getClass().getSimpleName();
    }
    @Override
    public void onBackPressed() {
        mPresenter.onBackPressed();
    }


    /* MVP View methods implementation */
    @Override
    public void loadViewForUrl(@NonNull Uri url) {
        WebUtil.openUrl(this, url);
    }

    @Override
    public int getTabCount() {
        return 5;
    }
    @NonNull
    @Override
    public Pair<Integer, MainFragment> createInitialView(int tabIndex) {
        MainFragment fragment;
        switch (tabIndex) {
            case 0:
                fragment = DashboardFragment.newInstance();
                break;
            case 1:
                fragment = NewsFragment.newInstance();
                break;
            case 2:
                fragment = CourseFragment.newInstance();
                break;
            case 3:
                fragment = HomeworkFragment.newInstance();
                break;
            case 4:
                fragment = FileOverviewFragment.newInstance();
                break;

            default:
                throw new IllegalArgumentException("newTabIndex could not be found");
        }
        return new Pair<>(fragment.getActivityId(), fragment);
    }

    @Override
    public void showView(int oldViewId, int newViewId, @Nullable MainFragment newView,
            int oldTabIndex, int newTabIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (newTabIndex > oldTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        else if (newTabIndex < oldTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        if (oldViewId >= 0)
            transaction.detach(findFragment(oldViewId));

        if (newView != null)
            transaction.add(R.id.content, newView, "" + newViewId);
        else
            transaction.attach(findFragment(newViewId));

        transaction.commitNow();
    }
    @Override
    public void removeViews(@NonNull List<Integer> viewIds) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setAllowOptimization(true);
        for (int viewId : viewIds)
            transaction.remove(findFragment(viewId));
        transaction.commit();
    }
    @Override
    public boolean currentViewHandlesBack(int viewId) {
        return findFragment(viewId).onBackPressed();
    }
    private MainFragment findFragment(int viewId) {
        return (MainFragment) getSupportFragmentManager().findFragmentByTag("" + viewId);
    }
    @NonNull
    public MainFragment getCurrentFragment() {
        return findFragment(mPresenter.getCurrentViewId());
    }

    public void addFragment(@NonNull MainFragment parent, @NonNull MainFragment child) {
        mPresenter.addView(parent.getActivityId(), child.getActivityId(), child);
    }
    public void removeFragment(@NonNull MainFragment fragment) {
        mPresenter.removeView(fragment.getActivityId());
    }
}
