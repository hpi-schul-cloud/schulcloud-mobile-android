package org.schulcloud.mobile.ui.dashboard;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.Pair;

import java.util.List;

public interface DashboardMvpView extends MvpView {

    void showOpenHomework(@NonNull Pair<String, String> openHomework);

    void showEvents(@NonNull List<Event> eventsForDay);

    void showCourseDetails(@NonNull String courseId);

}
