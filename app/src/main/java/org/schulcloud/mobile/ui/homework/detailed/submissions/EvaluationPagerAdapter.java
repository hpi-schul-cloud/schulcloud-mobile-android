package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.RealmString;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.homework.detailed.feedback.FeedbackFragment;
import org.schulcloud.mobile.ui.homework.detailed.submission.SubmissionFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Date: 4/25/2018
 */
public class EvaluationPagerAdapter extends FragmentPagerAdapter {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TAB_INVALID,
            TAB_SUBMISSION,
            TAB_FEEDBACK})
    public @interface Tab {}

    private static final int TAB_INVALID = 0;
    private static final int TAB_SUBMISSION = 1;
    private static final int TAB_FEEDBACK = 2;

    private Context mContext;
    private String mCurrentUserId;
    private User mStudent;
    private Homework mHomework;
    private Submission mSubmission;

    public EvaluationPagerAdapter(@NonNull Context context, @NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void setSubmission(@NonNull String currentUserId, @NonNull User student,
            @NonNull Homework homework, @Nullable Submission submission) {
        mCurrentUserId = currentUserId;
        mStudent = student;
        mHomework = homework;
        mSubmission = submission;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        switch (getTabType(position)) {
            case TAB_SUBMISSION:
                return SubmissionFragment.newInstance(mHomework._id, mStudent._id);
            case TAB_FEEDBACK:
                return FeedbackFragment.newInstance(mHomework._id, mStudent._id);

            case TAB_INVALID:
            default:
                return null;
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        int titleId;
        switch (getTabType(position)) {
            case TAB_SUBMISSION:
                titleId = R.string.homework_detailed_submission;
                break;
            case TAB_FEEDBACK:
                titleId = R.string.homework_detailed_feedback;
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
            return TAB_SUBMISSION;

        boolean isTeacher = isTeacher();
        if (isTeacher && position == 1)
            return TAB_FEEDBACK;

        return TAB_INVALID;
    }
    /**
     * @return Whether the user is the teacher or one of the substitution teachers associated with the current homework.
     */
    private boolean isTeacher() {
        if (mHomework.teacherId != null && mHomework.teacherId.equalsIgnoreCase(mCurrentUserId))
            return true;
        if (mHomework.courseId.substitutionIds != null)
            for (RealmString substitution : mHomework.courseId.substitutionIds)
                if (substitution.getValue().equalsIgnoreCase(mCurrentUserId))
                    return true;
        return false;
    }
}
