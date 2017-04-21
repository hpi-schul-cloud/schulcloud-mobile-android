package org.schulcloud.mobile.ui.files;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.main.MainMvpView;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by niklaskiefer on 21.04.17.
 */

public class FilePresenter extends BasePresenter<FileMvpView> {
    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public FilePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(FileMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadFiles() {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        // todo: get correct storage context
        mSubscription = mDataManager.getFiles("users/0000d213816abba584714c0a")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        files -> {
                            System.out.println(files);
                            getMvpView().showFiles(files);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            getMvpView().showError();
                        });
    }
}

