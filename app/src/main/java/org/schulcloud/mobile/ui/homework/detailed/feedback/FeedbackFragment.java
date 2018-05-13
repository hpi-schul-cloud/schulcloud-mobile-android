package org.schulcloud.mobile.ui.homework.detailed.feedback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.common.ContentWebView;
import org.schulcloud.mobile.ui.homework.detailed.StudentDependentFragment;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.ViewUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 5/4/2018
 */
public class FeedbackFragment extends MainFragment<FeedbackMvpView, FeedbackPresenter>
        implements FeedbackMvpView, StudentDependentFragment {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";
    private static final String ARGUMENT_STUDENT_ID = "ARGUMENT_STUDENT_ID";

    @Inject
    FeedbackPresenter mPresenter;

    @BindView(R.id.homeworkDetailedFeedback_tv_grade)
    TextView vTv_grade;
    @BindView(R.id.homeworkDetailedFeedback_cwv_gradeComment)
    ContentWebView vCwv_gradeComment;

    @NonNull
    public static FeedbackFragment newInstance(@NonNull String homeworkId,
            @NonNull String studentId) {
        FeedbackFragment feedbackFragment = new FeedbackFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
        args.putString(ARGUMENT_STUDENT_ID, studentId);
        feedbackFragment.setArguments(args);

        return feedbackFragment;
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
        String homeworkId = getArguments().getString(ARGUMENT_HOMEWORK_ID);
        if (homeworkId == null)
            throw new IllegalArgumentException("homeworkId must not be null");
        mPresenter.setHomework(homeworkId);

        String studentId = getArguments().getString(ARGUMENT_STUDENT_ID);
        if (studentId == null)
            throw new IllegalArgumentException("studentId must not be null");
        mPresenter.setStudent(studentId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_homework_detailed_feedback, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void update(@NonNull String studentId) {
        mPresenter.setStudent(studentId);
    }


    /* MVP View methods implementation */
    @Override
    public void showGrade(@Nullable Integer grade, @Nullable String gradeComment) {
        ViewUtil.setVisibility(vTv_grade, grade != null);
        if (grade != null)
            vTv_grade.setText(
                    Html.fromHtml(getString(R.string.homework_detailed_feedback_grade, grade)));

        vCwv_gradeComment.setContent(!TextUtils.isEmpty(gradeComment) ? gradeComment
                : getString(R.string.homework_detailed_feedback_error_empty));
    }
}
