package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.BaseFragment;

import java.util.Collections;
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

    @Inject
    SubmissionsPresenter mPresenter;
    @Inject
    SubmissionsAdapter mAdapter;
    @BindView(R.id.homeworkDetailedSubmissions_recycler)
    RecyclerView vRecycler;

    @NonNull
    public static SubmissionsFragment newInstance(@NonNull String homeworkId) {
        SubmissionsFragment submissionsFragment = new SubmissionsFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
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
        String id = getArguments().getString(ARGUMENT_HOMEWORK_ID);
        if (id == null)
            throw new IllegalArgumentException("id must not be null");
        mPresenter.loadSubmissions(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view =
                inflater.inflate(R.layout.fragment_homework_detailed_submissions, container, false);
        ButterKnife.bind(this, view);

        vRecycler.setAdapter(mAdapter);
        vRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    /* MVP View methods implementation */
    @Override
    public void showError() {
        // TODO: Show message
    }
    @Override
    public void showSubmissions(@NonNull String currentUserId, @NonNull Homework homework,
            @NonNull List<Pair<User, Submission>> submissions) {
        mAdapter.setSubmissions(currentUserId, homework, submissions);
    }
    @Override
    public void showSubmissionsEmpty(@NonNull String currentUserId, @NonNull Homework homework) {
        mAdapter.setSubmissions(currentUserId, homework, Collections.emptyList());
        // TODO: Show message
    }
}
