package org.schulcloud.mobile.ui.homework;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface HomeworkMvpView extends MvpView {

    void showHomework(List<Homework> homeworks);

    void showHomeworkEmpty();

    void showHomeworkDialog(String course, String title, String message);

    void showError();

}
