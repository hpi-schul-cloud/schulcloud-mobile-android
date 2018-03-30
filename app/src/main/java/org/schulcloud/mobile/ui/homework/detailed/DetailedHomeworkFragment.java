package org.schulcloud.mobile.ui.homework.detailed;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Comment;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.common.ContentWebView;
import org.schulcloud.mobile.ui.main.MainFragment;
import org.schulcloud.mobile.util.ViewUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @BindView(R.id.homeworkDetailed_tv_name)
    TextView vTv_name;
    @BindView(R.id.homeworkDetailed_cwv_description)
    ContentWebView vCwv_description;
    @BindView(R.id.homeworkDetailed_tv_due)
    TextView vTv_due;
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
    @SuppressLint("AddJavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_homework, container, false);
        ButterKnife.bind(this, view);
        setTitle(R.string.homework_homework_title);

        recyclerView.setAdapter(mCommentsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    /* MVP View methods implementation */
    @Override
    public void showHomework(@NonNull Homework homework) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        SimpleDateFormat dateFormatDeux = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date untilDate = null;
        try {
            untilDate = dateFormat.parse(homework.dueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (homework.courseId != null && homework.courseId.name != null)
            vTv_name.setText(getString(R.string.homework_homework_name_format,
                    homework.courseId.name, homework.name));
        else
            vTv_name.setText(homework.name);

        vCwv_description.setContent(homework.description);

        ViewUtil.setVisibility(vTv_due, untilDate != null);
        if (untilDate != null) {
            String dateTitle = getContext().getString(R.string.homework_homework_due);
            SpannableString dateText =
                    new SpannableString(dateTitle + dateFormatDeux.format(untilDate));
            if (untilDate.before(new Date()))
                dateText.setSpan(new StrikethroughSpan(), dateTitle.length(), dateText.length(),
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            vTv_due.setText(dateText);
        }
    }

    @Override
    public void showSubmission(@Nullable Submission submission, String userId) {
        nonPrivate.setVisibility(View.VISIBLE);

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
    }
}
