package org.schulcloud.mobile.ui.homework.detailed;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.RealmString;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.homework.detailed.details.DetailsFragment;
import org.schulcloud.mobile.ui.homework.detailed.submission.SubmissionFragment;
import org.schulcloud.mobile.util.ListUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

/**
 * Date: 4/25/2018
 */
public class HomeworkPagerAdapter extends FragmentPagerAdapter {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TAB_INVALID,
            TAB_DETAILS,
            TAB_SUBMISSION,
            TAB_SUBMISSIONS,
            TAB_FEEDBACK})
    public @interface Tab {}

    private static final int TAB_INVALID = 0;
    private static final int TAB_DETAILS = 1;
    private static final int TAB_SUBMISSION = 2;
    private static final int TAB_SUBMISSIONS = 3;
    private static final int TAB_FEEDBACK = 4;

    private Context mContext;
    private String mUserId;
    private Homework mHomework;

    public HomeworkPagerAdapter(@NonNull Context context, @NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    public void setHomework(@NonNull Homework homework, @NonNull String userId) {
        mUserId = userId;
        mHomework = homework;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        switch (getTabType(position)) {
            case TAB_DETAILS:
                return DetailsFragment.newInstance(mHomework._id);
            case TAB_SUBMISSION:
                return SubmissionFragment.newInstance(mHomework._id, mUserId);
            case TAB_SUBMISSIONS: // TODO: 4/27/2018
                return DetailsFragment.newInstance(mHomework._id);
            case TAB_FEEDBACK: // TODO: 4/27/2018
                return DetailsFragment.newInstance(mHomework._id);

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
            case TAB_SUBMISSIONS:
                titleId = R.string.homework_detailed_submissions;
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
            return TAB_DETAILS;

        boolean isTeacher = isTeacher();
        if (position == 1)
            if (isTeacher)
                return TAB_SUBMISSIONS;
            else
                return TAB_SUBMISSION;

        if (position == 2 && !isTeacher)
            return TAB_FEEDBACK;
        if (position == 3 && !isTeacher && mHomework.publicSubmissions != null
                && mHomework.publicSubmissions)
            return TAB_SUBMISSIONS;

        return TAB_INVALID;
    }
    /**
     * @return Whether the user is the teacher or one of the substitution teachers associated with the current homework.
     */
    private boolean isTeacher() {
        if (mHomework.teacherId != null && mHomework.teacherId.equalsIgnoreCase(mUserId))
            return true;
        if (mHomework.courseId.substitutionIds != null)
            for (RealmString substitution : mHomework.courseId.substitutionIds)
                if (substitution.getValue().equalsIgnoreCase(mUserId))
                    return true;
        return false;
    }
}
