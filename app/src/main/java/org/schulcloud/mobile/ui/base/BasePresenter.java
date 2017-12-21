package org.schulcloud.mobile.ui.base;

import android.content.Context;

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
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<T extends MvpView> implements Presenter<T> {

    public UserDataManager mUserDataManager;
    public EventDataManager mEventDataManager;
    public FeedbackDataManager mFeedbackDataManager;
    public FileDataManager mFileDataManager;
    public HomeworkDataManager mHomeworkDataManager;
    public NewsDataManager mNewsDataManager;
    public NotificationDataManager mNotificationDataManager;
    public SubmissionDataManager mSubmissionDataManager;
    public TopicDataManager mTopicDataManager;
    public CourseDataManager mCourseDataManager;

    public Subscription mSubscription;
    private T mMvpView;

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public T getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached())
            throw new MvpViewNotAttachedException();
    }

    /**
     * Checks whether there is already a logged-in user, if not so go to sign-in screen
     */
    public void isAlreadySignedIn(UserDataManager dataManager, Context context) {
        // 1. try to get currentUser from prefs
        String currentUserId = dataManager.getCurrentUserId();

        // value is "null" as String if pref does not exist
        if (currentUserId.equals("null")) {
            getMvpView().goToSignIn();
            return;
        }

        // 2. if there is a valid jwt in the storage (just online)
        if (NetworkUtil.isNetworkConnected(context)) {
            RxUtil.unsubscribe(mSubscription);
            mSubscription = dataManager.syncCurrentUser(currentUserId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            // onNext
                            currentUser -> {},
                            // onError, check failed
                            error -> {
                                Timber.e(error, "There was an error while fetching currentUser.");
                                getMvpView().goToSignIn();
                            });
        }
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
