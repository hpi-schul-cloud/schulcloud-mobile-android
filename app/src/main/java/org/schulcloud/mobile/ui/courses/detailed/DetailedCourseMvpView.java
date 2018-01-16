package org.schulcloud.mobile.ui.courses.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface DetailedCourseMvpView extends MvpView {

    void showCourse(@NonNull Course course);

    void showTopics(@NonNull List<Topic> topics);

    void showTopicDetail(@NonNull String topicId, @NonNull String topicName);

    void showError();
}
