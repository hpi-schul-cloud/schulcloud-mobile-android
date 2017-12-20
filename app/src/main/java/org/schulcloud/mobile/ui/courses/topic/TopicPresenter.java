package org.schulcloud.mobile.ui.courses.topic;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {

    @Inject
    public TopicPresenter(TopicDataManager topicDataManager) {
        mTopicDataManager = topicDataManager;
    }

    public void loadContents(String topicId) {
        checkViewAttached();
        getMvpView().showContent(mTopicDataManager.getContents(topicId));
    }
}
