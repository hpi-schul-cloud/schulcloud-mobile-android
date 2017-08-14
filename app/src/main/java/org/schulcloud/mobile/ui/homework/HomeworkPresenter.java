package org.schulcloud.mobile.ui.homework;

import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
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
    public HomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(HomeworkMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadHomework() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getHomework()
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
                    public void onNext(List<Homework> homeworks) {
                        if (homeworks.isEmpty()) {
                            getMvpView().showHomeworkEmpty();
                        } else {
                            getMvpView().showHomework(homeworks);
                        }
                    }
                });
    }

    public void checkSignedIn(Context context) {
        super.isAlreadySignedIn(mDataManager, context);
    }

    public void showHomeworkDetail(String homeworkId) {
        getMvpView().showHomeworkDialog(homeworkId);
    }

}
