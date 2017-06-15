package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedCourseMvpView extends MvpView {

    void showCourse(Course course);

    void showError();
}
