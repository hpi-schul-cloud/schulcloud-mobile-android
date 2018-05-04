package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedHomeworkMvpView extends MvpView {

    void showHomework(@NonNull Homework homework, @NonNull String userId);

    void showSubmission(@NonNull Submission submission, String userId);

}
