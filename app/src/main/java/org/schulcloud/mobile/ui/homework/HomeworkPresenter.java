package org.schulcloud.mobile.ui.homework;

import android.support.annotation.NonNull;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class HomeworkPresenter extends BasePresenter<HomeworkMvpView> {

    private final HomeworkDataManager mHomeworkDataManager;
    private final UserDataManager mUserDataManager;
    private Subscription mSubscription;
    private Subscription mCurrentUserSubscription;

    @Inject
    public HomeworkPresenter(HomeworkDataManager homeworkDataManager, UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mUserDataManager = userDataManager;
        loadHomework();
    }

    @Override
    public void onViewAttached(@NonNull HomeworkMvpView view) {
        super.onViewAttached(view);
        RxUtil.unsubscribe(mCurrentUserSubscription);
        mCurrentUserSubscription = mUserDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentUser ->
                        sendToView(v -> v.showCanCreateHomework(
                                currentUser.hasPermission(CurrentUser.PERMISSION_HOMEWORK_CREATE))));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mCurrentUserSubscription);
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadHomework() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mHomeworkDataManager.getHomework()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        homework -> {
                            if (homework.isEmpty())
                                sendToView(HomeworkMvpView::showHomeworkEmpty);
                            else
                                sendToView(view -> view.showHomework(homework));
                        },
                        throwable -> {
                            Timber.e(throwable, "There was an error loading the users.");
                            sendToView(HomeworkMvpView::showError);
                        });
    }

    public void showHomeworkDetail(@NonNull String homeworkId) {
        getViewOrThrow().showHomeworkDetail(homeworkId);
    }
}
