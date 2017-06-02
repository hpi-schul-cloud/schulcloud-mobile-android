package org.schulcloud.mobile.ui.homework.detailed;

import android.content.Context;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    @Inject
    public DetailedHomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DetailedHomeworkMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadHomework(String homeworkId) {
        checkViewAttached();
        getMvpView().showHomework(mDataManager.getHomeworkForId(homeworkId));
    }

    public void checkSignedIn(Context context) {
        super.isAlreadySignedIn(mDataManager, context);
    }

    public void showHomeworkDetail(String course, String title, String message) {
        getMvpView().showHomeworkDialog(course, title, message);
    }

}
