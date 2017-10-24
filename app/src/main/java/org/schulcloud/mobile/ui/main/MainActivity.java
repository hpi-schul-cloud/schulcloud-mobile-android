package org.schulcloud.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;

import butterknife.ButterKnife;

/**
 * MainActivity displays a BottomNavigationView and the Toolbar, the content is managed by fragments
 * which inherit from {@link MainFragment}.
 */
public final class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    //    public static final int TAB_DASHBOARD = R.id.main_navigation_dashboard;
    public static final int TAB_COURSES = R.id.main_navigation_courses;
    public static final int TAB_FILES = R.id.main_navigation_files;
    //    public static final int TAB_MATERIALS = R.id.main_navigation_materials;
    //    public static final int TAB_ADMINISTRATION = R.id.main_navigation_administration;

    private TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTabManager = new TabManager(this, R.id.content,
                (BottomNavigationView) findViewById(R.id.navigation));
        mTabManager.init(
                new int[]{
                        //                        TAB_DASHBOARD,
                        TAB_COURSES,
                        TAB_FILES,
                        //                        TAB_MATERIALS,
                        //                        TAB_ADMINISTRATION
                },
                new MainFragment[]{
                        null,
                        null});
    }
    public void addFragment(MainFragment parent, MainFragment child) {
        mTabManager.addFragment(parent, child);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(@Nullable Toolbar toolbar) {
        setSupportActionBar(toolbar);
    }
}
