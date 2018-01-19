package org.schulcloud.mobile.ui.courses.topic;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {

    private final DataManager mDataManager;
    private Topic mTopic;

    @Inject
    TopicPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }
    @Override
    public void onViewAttached(@NonNull TopicMvpView view) {
        super.onViewAttached(view);
        showName();
    }

    public void loadContents(@NonNull String topicId) {
        mTopic = mDataManager.getTopicForId(topicId);
        showName();
        sendToView(v -> v.showContent(mTopic.contents));
    }
    private void showName() {
        sendToView(v -> {
            if (mTopic == null)
                return;
            v.showName(mTopic.name);
        });
    }
}
