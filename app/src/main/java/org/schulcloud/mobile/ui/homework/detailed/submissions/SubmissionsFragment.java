package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 5/5/2018
 */
public class SubmissionsFragment extends BaseFragment<SubmissionsMvpView, SubmissionsPresenter>
        implements SubmissionsMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";
    private static final String ARGUMENT_SELECTED_STUDENT_ID = "ARGUMENT_SELECTED_STUDENT_ID";

    @Inject
    SubmissionsPresenter mPresenter;
    @Inject
    SubmissionsAdapter mAdapter;

    @BindView(R.id.homeworkDetailedSubmissions_tv_error)
    TextView vTv_error;
    @BindView(R.id.homeworkDetailedSubmissions_recycler)
    RecyclerView vRecycler;

    @NonNull
    public static SubmissionsFragment newInstance(@NonNull String homeworkId,
            @Nullable String selectedStudentId) {
        SubmissionsFragment submissionsFragment = new SubmissionsFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
        args.putString(ARGUMENT_SELECTED_STUDENT_ID, selectedStudentId);
        submissionsFragment.setArguments(args);

        return submissionsFragment;
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
        String id = args.getString(ARGUMENT_HOMEWORK_ID);
        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        String selectedUserId = args.getString(ARGUMENT_SELECTED_STUDENT_ID);
        mPresenter.loadSubmissions(id, selectedUserId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_homework_detailed_submissions, container, false);
        ButterKnife.bind(this, view);

        if (getParentFragment() instanceof SubmissionsAdapter.OnStudentSelectedListener) {
            SubmissionsAdapter.OnStudentSelectedListener listener =
                    (SubmissionsAdapter.OnStudentSelectedListener) getParentFragment();
            mAdapter.setOnStudentSelectedListener(listener);
        }
        vRecycler.setAdapter(mAdapter);
        vRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void showError(@StringRes int errorRes) {
        ViewUtil.setVisibility(vRecycler, false);
        ViewUtil.setVisibility(vTv_error, true);
        vTv_error.setText(errorRes);
    }


    /* MVP View methods implementation */
    @Override
    public void showError_courseNotFound() {
        showError(R.string.homework_detailed_submissions_error_courseNotFound);
    }
    @Override
    public void showError_courseEmpty() {
        showError(R.string.homework_detailed_submissions_error_courseEmpty);
    }
    @Override
    public void showSubmissions(@NonNull String currentUserId, @NonNull Homework homework,
            @NonNull List<Pair<User, Submission>> submissions, @Nullable String selectedUserId) {
        ViewUtil.setVisibility(vTv_error, false);
        ViewUtil.setVisibility(vRecycler, true);
        mAdapter.setSubmissions(currentUserId, homework, submissions, selectedUserId);
    }
}
