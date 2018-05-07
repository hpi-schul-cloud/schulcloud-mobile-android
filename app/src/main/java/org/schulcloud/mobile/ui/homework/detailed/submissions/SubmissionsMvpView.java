package org.schulcloud.mobile.ui.homework.detailed.submissions;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

/**
 * Date: 5/5/2018
 */
public interface SubmissionsMvpView extends MvpView {

    void showError();
    void showSubmissions(@NonNull String currentUserId, @NonNull Homework homework, @NonNull List<Pair<User, Submission>> submissions);
    void showSubmissionsEmpty(@NonNull String currentUserId, @NonNull Homework homework);

}
