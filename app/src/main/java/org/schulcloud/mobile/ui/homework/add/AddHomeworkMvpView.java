package org.schulcloud.mobile.ui.homework.add;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface AddHomeworkMvpView extends MvpView {

    void setCourses(@NonNull List<String> courses);

    void setCanCreatePublic(boolean canCreatePublic);

    void showHomeworkSaved();

    void reloadHomeworkList();

    void showCourseLoadingError();

    void showSaveError();

    void showNameEmpty();

    void showInvalidDates();

}
