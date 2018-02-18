package org.schulcloud.mobile.injection.component;

import org.schulcloud.mobile.injection.module.ActivityModule;
import org.schulcloud.mobile.injection.scope.PerActivity;
import org.schulcloud.mobile.ui.courses.CourseFragment;
import org.schulcloud.mobile.ui.courses.detailed.DetailedCourseFragment;
import org.schulcloud.mobile.ui.courses.topic.ContentAdapter;
import org.schulcloud.mobile.ui.courses.topic.TopicFragment;
import org.schulcloud.mobile.ui.dashboard.DashboardFragment;
import org.schulcloud.mobile.ui.feedback.FeedbackDialog;
import org.schulcloud.mobile.ui.files.FilesFragment;
import org.schulcloud.mobile.ui.files.overview.FileOverviewFragment;
import org.schulcloud.mobile.ui.homework.HomeworkFragment;
import org.schulcloud.mobile.ui.homework.add.AddHomeworkFragment;
import org.schulcloud.mobile.ui.homework.detailed.DetailedHomeworkFragment;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.news.NewsFragment;
import org.schulcloud.mobile.ui.news.detailed.DetailedNewsFragment;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.ui.signin.SignInActivity;
import org.schulcloud.mobile.ui.start.StartActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    //    Start
    void inject(StartActivity startActivity);

    //    Sign in
    void inject(SignInActivity signInActivity);

    //    Main
    void inject(MainActivity mainActivity);

    //    Dashboard
    void inject(DashboardFragment dashboardFragment);

    //    News
    void inject(NewsFragment newsFragment);
    void inject(DetailedNewsFragment detailedNewsFragment);

    //    Course
    void inject(CourseFragment courseFragment);
    void inject(DetailedCourseFragment detailedCourseFragment);
    void inject(TopicFragment topicFragment);

    void inject(ContentAdapter.WebViewHolder webViewHolder);
    void inject(ContentAdapter.ResourcesViewHolder resourcesViewHolder);

    //    Homework
    void inject(HomeworkFragment homeworkFragment);
    void inject(DetailedHomeworkFragment detailedHomeworkFragment);
    void inject(AddHomeworkFragment addHomeworkFragment);

    //    File
    void inject(FileOverviewFragment fileOverviewFragment);
    void inject(FilesFragment filesFragment);

    //    Settings
    void inject(SettingsActivity settingsActivity);

    //    Feedback
    void inject(FeedbackDialog feedbackDialog);

}
