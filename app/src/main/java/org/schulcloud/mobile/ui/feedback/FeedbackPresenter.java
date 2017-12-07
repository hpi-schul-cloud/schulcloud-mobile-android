package org.schulcloud.mobile.ui.feedback;

import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class FeedbackPresenter extends BasePresenter<FeedbackMvpView> {

    private DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    FeedbackPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }


    public void sendFeedback(@NonNull String format, @NonNull String email, @NonNull String content,
            @NonNull String contextName, @NonNull String currentUser,
            @NonNull String subject, @NonNull String to) {
        if (content.isEmpty())
            getViewOrThrow().showContentHint();
        else {
            String text = String.format(format, currentUser, email, contextName, content);
            FeedbackRequest feedbackRequest = new FeedbackRequest(text, subject, to);

            RxUtil.unsubscribe(mSubscription);
            mSubscription = mDataManager.sendFeedback(feedbackRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(feedbackResponse -> {},
                            throwable -> Log.e("Feedback", "onError", throwable),
                            () -> sendToView(FeedbackMvpView::showFeedbackSent));
        }
    }
}
