package org.schulcloud.mobile.ui.courses.topic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ConfigPersistent
public class TopicPresenter extends BasePresenter<TopicMvpView> {
    private static final String TAG = TopicPresenter.class.getSimpleName();

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

    public void loadContents(@Nullable String topicId) {
        if (topicId == null) {
            sendToView(TopicMvpView::showError);
            return;
        }

        mTopicDataManager.getTopic(topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topic -> {
                    mTopic = topic;
                    if (topic == null)
                        return;

                    showName();
                    sendToView(v -> v.showContent(mTopic.contents));
                }, throwable -> {
                    Log.w(TAG, "Error loading topic " + topicId, throwable);
                    sendToView(TopicMvpView::showError);
                });
    }

    private void showName() {
        sendToView(v -> {
            if (mTopic == null)
                return;
            v.showName(mTopic.name);
        });
    }
}
