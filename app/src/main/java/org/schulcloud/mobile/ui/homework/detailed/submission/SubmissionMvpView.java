package org.schulcloud.mobile.ui.homework.detailed.submission;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.ui.base.MvpView;

/**
 * Date: 4/27/2018
 */
interface SubmissionMvpView extends MvpView {

    void showError_notFound();
    void showComment(@Nullable String comment);

}
