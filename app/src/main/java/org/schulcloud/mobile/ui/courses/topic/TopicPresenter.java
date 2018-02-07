package org.schulcloud.mobile.ui.courses.topic;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {

    private final TopicDataManager mTopicDataManager;
    private Topic mTopic;

    @Inject
    public TopicPresenter(TopicDataManager topicDataManager) {
        mTopicDataManager = topicDataManager;
    }
    @Override
    public void onViewAttached(@NonNull TopicMvpView view) {
        super.onViewAttached(view);
        showName();
    }

    public void loadContents(@NonNull String topicId) {
        mTopic = mTopicDataManager.getTopicForId(topicId);
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
