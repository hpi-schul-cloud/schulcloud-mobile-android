package org.schulcloud.mobile.ui.courses.topic;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {

    private DataManager mDataManager;

    @Inject
    TopicPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void loadContents(@NonNull String topicId) {
        getViewOrThrow().showContent(mDataManager.getContents(topicId));
    }
}
