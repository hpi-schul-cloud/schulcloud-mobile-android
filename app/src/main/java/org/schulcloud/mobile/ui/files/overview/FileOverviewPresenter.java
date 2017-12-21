package org.schulcloud.mobile.ui.files.overview;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class FileOverviewPresenter extends BasePresenter<FileOverviewMvpView> {

    private final DataManager mDataManager;
    private Subscription mCoursesSubscription;

    @Inject
    public FileOverviewPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mCoursesSubscription);
    }
    public void load() {
        RxUtil.unsubscribe(mCoursesSubscription);
        mCoursesSubscription = mDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        courses -> sendToView(view -> view.showCourses(courses)),
                        throwable -> sendToView(FileOverviewMvpView::showCoursesError)
                );
    }

    public void showMyFiles() {
        mDataManager.setCurrentStorageContextToMy();
        getViewOrThrow().showDirectory();
    }
    public void showCourseDirectory(@NonNull String courseId) {
        mDataManager.setCurrentStorageContextToCourse(courseId);
        getViewOrThrow().showDirectory();
    }
}
