package org.schulcloud.mobile.test.common.injection.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.injection.ApplicationContext;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import static org.mockito.Mockito.mock;

/**
 * Provides application-level dependencies for an app running on a testing environment
 * This allows injecting mocks if necessary.
 */
@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    /************* MOCKS *************/

    @Provides
    @Singleton
    UserDataManager provideUserDataManager() {
        return mock(UserDataManager.class);
    }

    @Provides
    @Singleton
    EventDataManager provideEventDataManager() {
        return mock(EventDataManager.class);
    }

    @Provides
    @Singleton
    CourseDataManager provideCourseDataManager() {
        return mock(CourseDataManager.class);
    }

    @Provides
    @Singleton
    FeedbackDataManager provideFeedbackDataManager() {
        return mock(FeedbackDataManager.class);
    }

    @Provides
    @Singleton
    FileDataManager provideFileDataManager() {
        return mock(FileDataManager.class);
    }

    @Provides
    @Singleton
    HomeworkDataManager provideHomeworkDataManager() {
        return mock(HomeworkDataManager.class);
    }

    @Provides
    @Singleton
    NewsDataManager provideNewsDataManager() {
        return mock(NewsDataManager.class);
    }

    @Provides
    @Singleton
    NotificationDataManager provideNotificationDataManager() {
        return mock(NotificationDataManager.class);
    }

    @Provides
    @Singleton
    SubmissionDataManager provideSubmissionDataManager() {
        return mock(SubmissionDataManager.class);
    }

    @Provides
    @Singleton
    TopicDataManager provideTopicDataManager() {
        return mock(TopicDataManager.class);
    }

    @Provides
    @Singleton
    RestService provideRestService() {
        return mock(RestService.class);
    }

}
