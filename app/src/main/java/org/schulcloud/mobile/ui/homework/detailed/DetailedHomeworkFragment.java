package org.schulcloud.mobile.ui.homework.detailed;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.ui.base.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedHomeworkFragment extends BaseFragment implements DetailedHomeworkMvpView {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "org.schulcloud.mobile.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";

    private String homeworkId = null;

    @Inject
    DetailedHomeworkPresenter mDetailedHomeworkPresenter;

    @BindView(R.id.homeworkName)
    TextView homeworkName;

    @BindView(R.id.homeworkDescription)
    TextView homeworkDescription;

    @BindView(R.id.homeworkDue)
    TextView homeworkDueDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activityComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_detailed_homework, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        homeworkId = args.getString("homeworkId");

        mDetailedHomeworkPresenter.attachView(this);
        mDetailedHomeworkPresenter.loadHomework(homeworkId);

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
            homeworkDueDate.setPaintFlags(homeworkDueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (homework.courseId != null && homework.courseId.name != null)
            homeworkName.setText(String.format("%s %s",
                    "[" + homework.courseId.name + "]", homework.name));
        else
            homeworkName.setText(homework.name);
        homeworkDescription.setText(Html.fromHtml(homework.description));

        if (untilDate != null)
            homeworkDueDate.setText(dateFormatDeux.format(untilDate));

        String course = homework.courseId != null ? homework.courseId.name : "";

        //homeworkName.setText(homework.name);
        //homeworkDescription.setText(Html.fromHtml(homework.description));
    }

    @Override
    public void showError() {

    }

    @Override
    public void showHomeworkDialog(String course, String title, String message) {

    }

    @Override
    public void goToSignIn() {

    }
}
