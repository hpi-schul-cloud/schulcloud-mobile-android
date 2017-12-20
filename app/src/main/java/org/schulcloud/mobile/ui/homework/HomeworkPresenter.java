package org.schulcloud.mobile.ui.homework;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class HomeworkPresenter extends BasePresenter<HomeworkMvpView> {

    @Inject
    public HomeworkPresenter(HomeworkDataManager homeworkDataManager,
                             UserDataManager userDataManager) {
        mHomeworkDataManager = homeworkDataManager;
        mUserDataManager = userDataManager;
    }

    @Override
    public void attachView(HomeworkMvpView mvpView) {
        super.attachView(mvpView);
        mUserDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentUser ->
                        getMvpView().showCanCreateHomework(
                                currentUser.hasPermission(CurrentUser.PERMISSION_HOMEWORK_CREATE)));
    }
    @Override
    public void detachView() {
        super.detachView();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadHomework() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mHomeworkDataManager.getHomework()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Homework>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "There was an error loading the users.");
                        getMvpView().showError();
                    }

                    @Override
                    public void onNext(List<Homework> homework) {
                        if (homework.isEmpty()) {
                            getMvpView().showHomeworkEmpty();
                        } else {
                            getMvpView().showHomework(homework);
                        }
                    }
                });
    }

    public void showHomeworkDetail(String homeworkId) {
        getMvpView().showHomeworkDetail(homeworkId);
    }
}
