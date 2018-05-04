package org.schulcloud.mobile.ui.homework.detailed.details;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Date: 4/27/2018
 */
@ConfigPersistent
public class DetailsPresenter extends BasePresenter<DetailsMvpView> {
    private final HomeworkDataManager mDataManager;
    private Homework mHomework;

    @Inject
    public DetailsPresenter(HomeworkDataManager dataManager) {
        mDataManager = dataManager;
    }
    @Override
    public void onViewAttached(@NonNull DetailsMvpView view) {
        super.onViewAttached(view);
        showDescription();
    }

    void loadHomework(@NonNull String id) {
        mHomework = mDataManager.getHomeworkForId(id);
    }
    private void showDescription() {
        sendToView(v -> {
            if (mHomework == null)
                return;
            v.showDescription(mHomework.description);
        });
    }
}
