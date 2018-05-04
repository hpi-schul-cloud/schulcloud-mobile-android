package org.schulcloud.mobile.ui.homework.detailed.submission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.common.ContentWebView;
import org.schulcloud.mobile.ui.main.MainFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 4/27/2018
 */
public class SubmissionFragment extends MainFragment<SubmissionMvpView, SubmissionPresenter>
        implements SubmissionMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";
    private static final String ARGUMENT_STUDENT_ID = "ARGUMENT_STUDENT_ID";

    @Inject
    SubmissionPresenter mPresenter;

    @BindView(R.id.homeworkDetailedSubmission_cwv_content)
    ContentWebView vCwv_content;

    @NonNull
    public static SubmissionFragment newInstance(@NonNull String homeworkId,
            @NonNull String studentId) {
        SubmissionFragment submissionFragment = new SubmissionFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
        args.putString(ARGUMENT_STUDENT_ID, studentId);
        submissionFragment.setArguments(args);

        return submissionFragment;
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

        String studentId = getArguments().getString(ARGUMENT_STUDENT_ID);
        if (studentId == null)
            throw new IllegalArgumentException("studentId must not be null");

        mPresenter.loadSubmission(homeworkId, studentId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_homework_detailed_submission, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    /* MVP View methods implementation */
    @Override
    public void showError_notFound() {

    }
    @Override
    public void showComment(@Nullable String comment) {
        vCwv_content.setContent(comment);
    }
}
