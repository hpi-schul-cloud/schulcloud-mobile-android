package org.schulcloud.mobile.ui.homework.detailed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.main.MainFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedHomeworkFragment
        extends MainFragment<DetailedHomeworkMvpView, DetailedHomeworkPresenter>
        implements DetailedHomeworkMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";

    @Inject
    DetailedHomeworkPresenter mPresenter;

    @Inject
    CommentsAdapter mCommentsAdapter;

    @BindView(R.id.homeworkDetailed_toolbar)
    Toolbar vToolbar;
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
        DetailedHomeworkFragment detailedHomeworkFragment = new DetailedHomeworkFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_HOMEWORK_ID, homeworkId);
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
        String homeworkId = getArguments().getString(ARGUMENT_HOMEWORK_ID);
        if (homeworkId == null)
            throw new IllegalArgumentException("homeworkId must not be null");
        mPresenter.loadHomework(homeworkId);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_detailed, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_homework_title);

        mPagerAdapter = new HomeworkPagerAdapter(getContext(), getChildFragmentManager());
        Pair<Homework, String> hwAndId = mPresenter.getHomeworkAndUserId();
        mPagerAdapter.setHomework(hwAndId.first, hwAndId.second);

        vVp_content.setAdapter(mPagerAdapter);
        vTl_tabs.setupWithViewPager(vVp_content);

        return view;
    }
    @Nullable
    @Override
    protected Toolbar getToolbar() {
        return vToolbar;
    }


    /* MVP View methods implementation */
    @Override
    public void showHomework(@NonNull Homework homework, @NonNull String userId) {
        mPagerAdapter.setHomework(homework, userId);
    }
}
