package org.schulcloud.mobile.ui.homework;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface HomeworkMvpView extends MvpView {

    void showHomework(@NonNull List<Homework> homework);

    void showCanCreateHomework(boolean canCreateHomework);

    void showHomeworkEmpty();

    void showHomeworkDetail(@NonNull String homeworkId);

    void showError();

}
