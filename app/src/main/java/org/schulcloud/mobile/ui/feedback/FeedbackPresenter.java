package org.schulcloud.mobile.ui.feedback;

import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class FeedbackPresenter extends BasePresenter<FeedbackMvpView> {

    private final FeedbackDataManager mFeedbackDataManager;
    private final UserDataManager mUserDataManager;
    private Subscription mSubscription;

    private String mContextName;

    @Inject
    public FeedbackPresenter(FeedbackDataManager feedbackDataManager, UserDataManager userDataManager) {
        mFeedbackDataManager = feedbackDataManager;
        mUserDataManager = userDataManager;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mSubscription);
    }

    public void init(@NonNull String contextName) {
        mContextName = contextName;
    }
    public void sendFeedback(@NonNull String format, @NonNull String email, @NonNull String content,
            @NonNull String subject, @NonNull String to) {
        if (content.isEmpty())
            getViewOrThrow().showError_contentEmpty();
        else {
            String text = String
                    .format(format, mUserDataManager, email.trim(), mContextName,
                            content.trim());
            FeedbackRequest feedbackRequest = new FeedbackRequest(text, subject, to);

            RxUtil.unsubscribe(mSubscription);
            mSubscription = mFeedbackDataManager.sendFeedback(feedbackRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            feedbackResponse -> sendToView(FeedbackMvpView::showFeedbackSent),
                            throwable -> Log.e("Feedback", "onError", throwable));
        }
    }
}
