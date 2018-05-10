package org.schulcloud.mobile.ui.homework.detailed;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.homework.detailed.submissions.SubmissionsAdapter;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.ModelUtil;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedHomeworkFragment
        extends MainFragment<DetailedHomeworkMvpView, DetailedHomeworkPresenter>
        implements DetailedHomeworkMvpView, SubmissionsAdapter.OnStudentSelectedListener {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";
    private static final String ARGUMENT_STUDENT_ID = "ARGUMENT_STUDENT_ID";

    @Inject
    DetailedHomeworkPresenter mPresenter;

    @Inject
    CommentsAdapter mCommentsAdapter;

    @BindView(R.id.homeworkDetailed_toolbar)
    Toolbar vToolbar;
    @BindView(R.id.homeworkDetailed_v_courseColor)
    View vV_courseColor;
    @BindView(R.id.homeworkDetailed_atv_private)
    AwesomeTextView vAtv_private;
    @BindView(R.id.homeworkDetailed_tv_title)
    TextView vTv_title;
    @BindView(R.id.homeworkDetailed_tv_subtitle)
    TextView vTv_subtitle;
    @BindView(R.id.homeworkDetailed_tl_tabs)
    TabLayout vTl_tabs;
    @BindView(R.id.homeworkDetailed_vp_content)
    ViewPager vVp_content;
    HomeworkPagerAdapter mPagerAdapter;

    /**
     * Creates a new instance of this fragment.
     *
     * @param homeworkId The ID of the homework to be shown.
     * @return The new instance
     */
    @NonNull
    public static DetailedHomeworkFragment newInstance(@NonNull String homeworkId) {
        return newInstance(homeworkId, null);
    }
    /**
     * Creates a new instance of this fragment showing the task and the submission of one student.
     *
     * @param homeworkId The ID of the homework to be shown.
     * @return The new instance
     */
    @NonNull
    public static DetailedHomeworkFragment newInstance(@NonNull String homeworkId,
            @Nullable String studentId) {
        DetailedHomeworkFragment detailedHomeworkFragment = new DetailedHomeworkFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
        args.putString(ARGUMENT_STUDENT_ID, studentId);
        detailedHomeworkFragment.setArguments(args);

        return detailedHomeworkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mPresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        String homeworkId = args.getString(ARGUMENT_HOMEWORK_ID);
        if (homeworkId == null)
            throw new IllegalArgumentException("homeworkId must not be null");
        String studentId = args.getString(ARGUMENT_STUDENT_ID);
        mPresenter.init(homeworkId, studentId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_detailed, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_homework_title);

        mPagerAdapter = new HomeworkPagerAdapter(getContext(), getChildFragmentManager());
        vVp_content.setAdapter(mPagerAdapter);
        vTl_tabs.setupWithViewPager(vVp_content);

        return view;
    }
    @Nullable
    @Override
    protected Toolbar getToolbar() {
        return vToolbar;
    }

    @Override
    public void onStudentSelected(@NonNull User student) {
        mPresenter.onStudentSelected(student);
    }

    /* MVP View methods implementation */
    @Override
    public void showError_notFound() {
        DialogFactory
                .createGenericErrorDialog(getContext(), "Die Hausaufgabe wurde nicht gefunden")
                .show();
    }
    @Override
    public void showHomework(@NonNull Homework homework, @Nullable User student,
            boolean switchToSubmission) {
        ViewConfig viewConfig = mPresenter.getViewConfig();
        if (viewConfig != null) {
            // If a submission was just selected, switch to the submission tab
            mPagerAdapter.setViewConfig(mPresenter.getViewConfig());
            if (switchToSubmission && student != null)
                vVp_content.setCurrentItem(2, false);
        }

        vV_courseColor.setBackgroundColor(Color.parseColor(homework.courseId.color));

        ViewUtil.setVisibility(vAtv_private, homework.isPrivate());

        vTv_title.setText(getString(R.string.homework_homework_name_format, homework.courseId.name,
                homework.name));

        String studentName = ModelUtil.getUserName(getContext(), student);
        ViewUtil.setVisibility(vTv_subtitle, studentName != null);
        if (studentName != null)
            vTv_subtitle
                    .setText(getString(R.string.homework_detailed_submission_hint, studentName));
    }
}
