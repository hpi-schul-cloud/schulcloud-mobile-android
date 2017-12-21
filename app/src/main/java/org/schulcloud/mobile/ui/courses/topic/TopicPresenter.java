package org.schulcloud.mobile.ui.courses.topic;

<<<<<<< 1faa04727458a2e67d8779f85a46c262c241d005
import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
<<<<<<< 7d98e070801387c77a122fd23a30f82e95e74393
import org.schulcloud.mobile.data.model.Topic;
=======
=======
>>>>>>> removed usage of DatabaseHelper and DatManager completely in code, cleaned up imports( removed DataManager and DatabaseHelper from Imports)
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {

    private final DataManager mDataManager;
    private Topic mTopic;

    @Inject
<<<<<<< 7d98e070801387c77a122fd23a30f82e95e74393
    TopicPresenter(DataManager dataManager) {
        mDataManager = dataManager;
=======
    public TopicPresenter(TopicDataManager topicDataManager) {
        mTopicDataManager = topicDataManager;
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
    }
    @Override
    public void onViewAttached(@NonNull TopicMvpView view) {
        super.onViewAttached(view);
        showName();
    }

<<<<<<< 7d98e070801387c77a122fd23a30f82e95e74393
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
=======
    public void loadContents(String topicId) {
        checkViewAttached();
        getMvpView().showContent(mTopicDataManager.getContents(topicId));
>>>>>>> split DataManagers/DatabaseHelpers and updated for the new builds, need to fix Tests
    }
}
