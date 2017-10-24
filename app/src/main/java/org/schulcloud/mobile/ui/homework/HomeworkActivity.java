package org.schulcloud.mobile.ui.homework;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.sync.HomeworkSyncService;
import org.schulcloud.mobile.data.sync.SubmissionSyncService;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.homework.add.AddHomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.util.DialogFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeworkActivity extends BaseActivity implements HomeworkMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.homework.HomeworkActivity.EXTRA_TRIGGER_SYNC_FLAG";

    @Inject
    public HomeworkPresenter mHomeworkPresenter;
    @Inject
    HomeworkAdapter mHomeworkAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.fab_add_homework)
    FloatingActionButton mFabAddHomework;
    @BindView(R.id.fab_add_homework_icon)
    AwesomeTextView mFabAddHomeworkIcon;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, HomeworkActivity.class);
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
        View contentView = inflater.inflate(R.layout.activity_homework, null, false);
        mDrawer.addView(contentView, 0);
        getSupportActionBar().setTitle(R.string.homework_title);
        ButterKnife.bind(this);


        mRecyclerView.setAdapter(mHomeworkAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHomeworkPresenter.attachView(this);
        mHomeworkPresenter.checkSignedIn(this);

        mHomeworkPresenter.loadHomework();

        if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
            startService(HomeworkSyncService.getStartIntent(this));
            startService(SubmissionSyncService.getStartIntent(this));
        }

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.hpiRed), getResources().getColor(R.color.hpiOrange), getResources().getColor(R.color.hpiYellow));

        swipeRefresh.setOnRefreshListener(
                () -> {
                    startService(HomeworkSyncService.getStartIntent(this));
                    startService(SubmissionSyncService.getStartIntent(this));

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        mHomeworkPresenter.loadHomework();

                        swipeRefresh.setRefreshing(false);
                    }, 3000);
                }
        );

        mFabAddHomework.setOnClickListener((view) -> {
            AddHomeworkFragment frag = new AddHomeworkFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.overlay_fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFabAddHomeworkIcon.setTextColor(ContextCompat.getColor(this, R.color.white));
    }
    @Override
    protected void onDestroy() {
        mHomeworkPresenter.detachView();
        super.onDestroy();
    }

    /***** MVP View methods implementation *****/

    @Override
    public void showHomework(List<Homework> homeworks) {
        mHomeworkAdapter.setHomework(homeworks);
        mHomeworkAdapter.setContext(this);
        mHomeworkAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(this, R.string.homework_loading_error)
                .show();
    }

    @Override
    public void showHomeworkEmpty() {
        mHomeworkAdapter.setHomework(Collections.emptyList());
        mHomeworkAdapter.notifyDataSetChanged();
    }

    @Override
    public void showHomeworkDialog(String homeworkId) {
        DetailedHomeworkFragment frag = new DetailedHomeworkFragment();
        Bundle args = new Bundle();
        args.putString(DetailedHomeworkFragment.ARGUMENT_HOMEWORK_ID, homeworkId);
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
