package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedHomeworkMvpView extends MvpView {

    void showError_notFound();
    void showHomework(@NonNull Homework homework, @NonNull String userId, @Nullable User student,
            boolean switchToSubmission);

}
