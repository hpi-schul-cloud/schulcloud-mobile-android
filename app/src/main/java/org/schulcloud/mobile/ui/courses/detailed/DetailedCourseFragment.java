package org.schulcloud.mobile.ui.courses.detailed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.sync.TopicSyncService;
import org.schulcloud.mobile.ui.courses.topic.TopicFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedCourseFragment
        extends MainFragment<DetailedCourseMvpView, DetailedCoursePresenter>
        implements DetailedCourseMvpView {
    private static final String ARGUMENT_COURSE_ID = "ARGUMENT_COURSE_ID";

    @Inject
    DetailedCoursePresenter mDetailedCoursePresenter;

    @Inject
    TopicsAdapter mTopicsAdapter;

    @BindView(R.id.courseName)
    TextView courseName;
    @BindView(R.id.topicRecycler)
    RecyclerView recyclerView;

    /**
     * Creates a new instance of this fragment.
     *
     * @param courseId The ID of the course that should be shown.
     * @return The new instance
     */
    @NonNull
    public static DetailedCourseFragment newInstance(@NonNull String courseId) {
        DetailedCourseFragment detailedCourseFragment = new DetailedCourseFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_COURSE_ID, courseId);
        detailedCourseFragment.setArguments(args);

        return detailedCourseFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mDetailedCoursePresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        String courseId = args.getString(ARGUMENT_COURSE_ID);
        mDetailedCoursePresenter.loadCourse(courseId);
        startService(TopicSyncService.getStartIntent(getContext(), courseId));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_course, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.courses_course_title);

        recyclerView.setAdapter(mTopicsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showCourseName(@NonNull String name) {
        courseName.setText(name);
    }

    @Override
    public void showTopics(@NonNull List<Topic> topics) {
        mTopicsAdapter.setTopics(topics);
    }

    @Override
    public void showTopicDetail(@NonNull String topicId) {
        addFragment(TopicFragment.newInstance(topicId));
    }

    @Override
    public void showError() {
        DialogFactory.createGenericErrorDialog(getContext(), R.string.courses_course_loading_error)
                .show();
    }
}
