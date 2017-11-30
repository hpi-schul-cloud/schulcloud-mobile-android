package org.schulcloud.mobile.ui.homework.detailed;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Comment;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.main.MainFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class DetailedHomeworkFragment extends MainFragment implements DetailedHomeworkMvpView {
    private static final String ARGUMENT_HOMEWORK_ID = "ARGUMENT_HOMEWORK_ID";

    @Inject
    DetailedHomeworkPresenter mDetailedHomeworkPresenter;

    @Inject
    CommentsAdapter mCommentsAdapter;

    private String mHomeworkId = null;

    @BindView(R.id.homeworkName)
    TextView homeworkName;
    @BindView(R.id.homeworkDescription)
    TextView homeworkDescription;
    @BindView(R.id.homeworkDue)
    TextView homeworkDueDate;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.grade)
    TextView grade;
    @BindView(R.id.gradeComment)
    TextView gradeComment;
    @BindView(R.id.nonPrivate)
    RelativeLayout nonPrivate;

    /**
     * Creates a new instance of this fragment.
     *
     * @param homeworkId The ID of the homework to be shown.
     * @return The new instance
     */
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

        mHomeworkId = getArguments().getString(ARGUMENT_HOMEWORK_ID);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_homework, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_homework_title);

        recyclerView.setAdapter(mCommentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDetailedHomeworkPresenter.attachView(this);
        mDetailedHomeworkPresenter.loadHomework(mHomeworkId);

        return view;
    }

    /***** MVP View methods implementation *****/
    @Override
    public void showHomework(Homework homework) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date untilDate = null;
        try {
            untilDate = dateFormat.parse(homework.dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (untilDate.before(new Date())) {
            homeworkDueDate.setPaintFlags(
                    homeworkDueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (homework.courseId != null && homework.courseId.name != null)
            homeworkName.setText(getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            homeworkName.setText(homework.name);
        homeworkDescription.setText(Html.fromHtml(homework.description));

        if (homework.restricted == null || !homework.restricted)
            mDetailedHomeworkPresenter.loadComments(mHomeworkId);
        else
            nonPrivate.setVisibility(View.GONE);

        if (untilDate != null)
            homeworkDueDate.setText(dateFormatDeux.format(untilDate));

        String course = homework.courseId != null ? homework.courseId.name : "";
    }

    @Override
    public void showSubmission(Submission submission, String userId) {
        if (submission == null) {
            submission = new Submission();
            submission.comment = getString(R.string.homework_homework_notSubmitted);
            submission.comments = new RealmList<>();
            Comment comment = new Comment();
            comment.comment = getString(R.string.homework_homework_comments_none);
            submission.comments.add(comment);
        }

        if (submission.grade != null)
            grade.setText(Integer.toString(submission.grade));
        if (submission.gradeComment != null)
            gradeComment.setText(Html.fromHtml(submission.gradeComment));

        mCommentsAdapter.setComments(submission.comments);
        mCommentsAdapter.setUserId(userId);
        mCommentsAdapter.notifyDataSetChanged();
    }
}
