package org.schulcloud.mobile.ui.homework.detailed;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.homework.detailed.details.DetailsFragment;
import org.schulcloud.mobile.ui.homework.detailed.feedback.FeedbackFragment;
import org.schulcloud.mobile.ui.homework.detailed.submission.SubmissionFragment;
import org.schulcloud.mobile.ui.homework.detailed.submissions.SubmissionsFragment;
import org.schulcloud.mobile.util.ModelUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Date: 4/25/2018
 */
public class HomeworkPagerAdapter extends FragmentPagerAdapter {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TAB_INVALID,
            TAB_DETAILS,
            TAB_SUBMISSION,
            TAB_FEEDBACK,
            TAB_SUBMISSIONS})
    public @interface Tab {}

    private static final int TAB_INVALID = 0;
    private static final int TAB_DETAILS = 1;
    private static final int TAB_SUBMISSION = 2;
    private static final int TAB_FEEDBACK = 3;
    private static final int TAB_SUBMISSIONS = 4;

    private Context mContext;
    private String mUserId;
    private String mStudentId;
    private Homework mHomework;

    public HomeworkPagerAdapter(@NonNull Context context, @NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void setHomework(@NonNull Homework homework, @NonNull String userId,
            @Nullable User student) {
        mUserId = userId;
        mHomework = homework;

        if (student != null)
            mStudentId = student._id;
        else if (!ModelUtil.isTeacherOf(mUserId, mHomework))
            mStudentId = mUserId;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        switch (getTabType(position)) {
            case TAB_DETAILS:
                return DetailsFragment.newInstance(mHomework._id);
            case TAB_SUBMISSION:
                return SubmissionFragment.newInstance(mHomework._id, mStudentId);
            case TAB_FEEDBACK:
                return FeedbackFragment.newInstance(mHomework._id, mStudentId);
            case TAB_SUBMISSIONS:
                return SubmissionsFragment.newInstance(mHomework._id, mStudentId);

            case TAB_INVALID:
            default:
                return null;
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        int titleId;
        switch (getTabType(position)) {
            case TAB_DETAILS:
                titleId = R.string.homework_detailed_details;
                break;
            case TAB_SUBMISSION:
                titleId = R.string.homework_detailed_submission;
                break;
            case TAB_FEEDBACK:
                titleId = R.string.homework_detailed_feedback;
                break;
            case TAB_SUBMISSIONS:
                titleId = R.string.homework_detailed_submissions;
                break;

            case TAB_INVALID:
            default:
                return null;
        }
        return mContext.getString(titleId);
    }
    @Override
    public int getCount() {
        int count = 0;
        //noinspection StatementWithEmptyBody
        while (getTabType(count++) != TAB_INVALID)
            ;
        return count - 1;
    }

    @Tab
    private int getTabType(int position) {
        if (mHomework == null)
            return TAB_INVALID;

        if (position == 0)
            return TAB_DETAILS;

        if (!mHomework.hasPrivateSubmissions() || ModelUtil.isTeacherOf(mUserId, mHomework)) {
            if (position == 1)
                return TAB_SUBMISSIONS;

            if (mStudentId != null) {
                if (position == 2)
                    return TAB_SUBMISSION;

                if (position == 3)
                    return TAB_FEEDBACK;
            }
        } else {
            if (position == 1)
                return TAB_SUBMISSION;

            if (position == 2)
                return TAB_FEEDBACK;

            if (position == 3)
                return TAB_SUBMISSIONS;
        }

        return TAB_INVALID;
    }
}
