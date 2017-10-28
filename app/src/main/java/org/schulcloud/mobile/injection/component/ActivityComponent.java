package org.schulcloud.mobile.injection.component;

import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.TopicFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardActivity;
import org.schulcloud.mobile.ui.feedback.FeedbackDialog;
import org.schulcloud.mobile.ui.homework.HomeworkActivity;
import org.schulcloud.mobile.ui.files.FileFragment;
import org.schulcloud.mobile.ui.homework.add.AddHomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    //    Base
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    //    Sign in
    void inject(SignInActivity signInActivity);

    //    Main
    void inject(MainActivity mainActivity);

    //    Dashboard
    void inject(DashboardActivity dashboardActivity);

    //    Course
    void inject(CourseFragment courseFragment);

    void inject(DetailedCourseFragment detailedCourseFragment);

    void inject(TopicFragment topicFragment);

    //    File
    void inject(FileFragment fileFragment);

    //    Homework
    void inject(HomeworkActivity homeworkActivity);

    void inject(DetailedHomeworkFragment detailedHomeworkFragment);

    void inject(AddHomeworkFragment addHomeworkFragment);

    //    Settings
    void inject(SettingsActivity settingsActivity);

    //    Feedback
    void inject(FeedbackDialog feedbackDialog);
}
