package org.schulcloud.mobile.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.RealmString;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;

/**
 * Date: 5/10/2018
 */
public final class ModelUtil {
    private ModelUtil() { }

    @Nullable
    public static String getUserName(@NonNull Context context, @Nullable User user) {
        if (user == null)
            return null;

        if (!TextUtils.isEmpty(user.displayName))
            return user.displayName;

        return context.getString(R.string.general_user_name,
                user.firstName, user.lastName);
    }
    @Nullable
    public static String getSubmissionGrade(@NonNull Context context,
            @Nullable Submission submission) {
        if (submission == null || submission.grade == null)
            return null;

        return context.getString(R.string.homework_submission_grade, submission.grade);
    }

    /**
     * @return Whether the user is the teacher or one of the substitution teachers associated with
     * the specified homework.
     */
    public static boolean isTeacherOf(@Nullable String userId, @Nullable Homework homework) {
        if (userId == null || homework == null)
            return false;

        if (homework.teacherId != null && homework.teacherId.equalsIgnoreCase(userId))
            return true;

        if (homework.courseId.substitutionIds != null)
            return ListUtils.contains(homework.courseId.substitutionIds,
                    s -> s.getValue().equalsIgnoreCase(userId));
        return false;
    }
}
