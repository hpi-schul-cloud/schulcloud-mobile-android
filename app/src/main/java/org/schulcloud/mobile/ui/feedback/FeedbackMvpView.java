package org.schulcloud.mobile.ui.feedback;

import org.schulcloud.mobile.ui.base.MvpView;

public interface FeedbackMvpView extends MvpView {
    void showError_contentEmpty();

    void showFeedbackSent();
}
