package org.schulcloud.mobile.ui.homework.detailed;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Comment;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.common.ContentWebView;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.FormatUtil;
import org.schulcloud.mobile.util.ViewUtil;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class DetailedHomeworkFragment
        extends MainFragment<DetailedHomeworkMvpView, DetailedHomeworkPresenter>
        implements DetailedHomeworkMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";

    @Inject
    DetailedHomeworkPresenter mDetailedHomeworkPresenter;

    @Inject
    CommentsAdapter mCommentsAdapter;

    /*@BindView(R.id.homeworkDetailed_v_color)
    View vV_color;
    @BindView(R.id.homeworkDetailed_tv_name)
    TextView vTv_name;
    @BindView(R.id.homeworkDetailed_cwv_description)
    ContentWebView vCwv_description;
    @BindView(R.id.homeworkDetailed_tv_dates)
    TextView vTv_dates;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.homeworkDetailed_tv_grade)
    TextView vTv_grade;
    @BindView(R.id.homeworkDetailed_cwv_gradeComment)
    ContentWebView vCwv_gradeComment;
    @BindView(R.id.nonPrivate)
    LinearLayout nonPrivate;*/

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
        setPresenter(mDetailedHomeworkPresenter);
        readArguments(savedInstanceState);
    }
    @Override
    public void onReadArguments(Bundle args) {
        mDetailedHomeworkPresenter.loadHomework(getArguments().getString(ARGUMENT_HOMEWORK_ID));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_detailed, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_homework_title);

        //recyclerView.setAdapter(mCommentsAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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


    /* MVP View methods implementation */
    @Override
    public void showHomework(@NonNull Homework homework, @NonNull String userId) {
        mPagerAdapter.setHomework(homework, userId);

        /*if (homework.courseId != null) {
            vV_color.setBackgroundColor(Color.parseColor(homework.courseId.color));
            vV_color.setVisibility(View.VISIBLE);
        }

        if (homework.courseId != null && homework.courseId.name != null)
            vTv_name.setText(getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            vTv_name.setText(homework.name);

        vCwv_description.setContent(homework.description);

        Date availableDate = FormatUtil.parseDate(homework.availableDate);
        Date dueDate = FormatUtil.parseDate(homework.dueDate);
        ViewUtil.setVisibility(vTv_dates, dueDate != null && availableDate != null);
        if (dueDate != null && availableDate != null)
            vTv_dates.setText(getContext().getString(R.string.homework_homework_dates,
                    FormatUtil.toUserString(availableDate), FormatUtil.toUserString(dueDate)));*/
    }

    @Override
    public void showSubmission(@Nullable Submission submission, String userId) {
        /*nonPrivate.setVisibility(View.VISIBLE);

        if (submission == null) {
            submission = new Submission();
            submission.comment = getString(R.string.homework_homework_notSubmitted);
            submission.comments = new RealmList<>();
            Comment comment = new Comment();
            comment.comment = getString(R.string.homework_homework_comments_none);
            submission.comments.add(comment);
        }

        if (submission.grade != null) {
            vTv_grade.setVisibility(View.VISIBLE);
            vTv_grade.setText(getString(R.string.homework_homework_grade, submission.grade));
        }

        if (submission.gradeComment == null)
            vCwv_description.setContent(getString(R.string.homework_homework_gradeComment_none));
        else
            vCwv_description.setContent(submission.gradeComment);

        mCommentsAdapter.setComments(submission.comments);
        mCommentsAdapter.setUserId(userId);*/
    }
}
