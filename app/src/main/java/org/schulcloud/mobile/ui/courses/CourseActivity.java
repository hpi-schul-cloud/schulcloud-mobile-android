package org.schulcloud.mobile.ui.courses;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.sync.CourseSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseActivity extends BaseActivity implements CourseMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    CoursePresenter mCoursePresenter;
    @Inject
    CourseAdapter mCourseAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, CourseActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        //setContentView(R.layout.activity_main);

        LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_course, null, false);
        mDrawer.addView(contentView, 0);
        getSupportActionBar().setTitle(R.string.title_courses);
        ButterKnife.bind(this);


        mRecyclerView.setAdapter(mCourseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCoursePresenter.attachView(this);
        mCoursePresenter.checkSignedIn(this);

        mCoursePresenter.loadCourses();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(CourseSyncService.getStartIntent(this));
        }
    }

    @Override
    protected void onDestroy() {
        mCoursePresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showCourses(List<Course> courses) {
        mCourseAdapter.setCourses(courses);
        mCourseAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, "Leider gab es ein Problem beim fetchen der Kurse")
                .show();
    }

    @Override
    public void showCoursesEmpty() {
        mCourseAdapter.setCourses(Collections.emptyList());
        mCourseAdapter.notifyDataSetChanged();
    }

    @Override
    public void showCourseDialog(String courseId) {
        DetailedCourseFragment frag = new DetailedCourseFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        frag.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overlay_fragment_container, frag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        this.startActivity(intent);
    }
}
