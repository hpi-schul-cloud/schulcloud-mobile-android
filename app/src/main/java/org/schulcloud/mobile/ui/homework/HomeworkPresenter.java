package org.schulcloud.mobile.ui.homework;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class HomeworkPresenter extends BasePresenter<HomeworkMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public HomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadHomework() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mDataManager.getHomework()
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
