package org.schulcloud.mobile.ui.homework.detailed;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedHomeworkMvpView extends MvpView {

    void showHomework(Homework homework);

    void showHomeworkDialog(String course, String title, String message);

    void showError();

    void showSubmission(Submission submission);
}
