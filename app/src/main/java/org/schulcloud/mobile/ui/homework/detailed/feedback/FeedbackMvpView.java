package org.schulcloud.mobile.ui.homework.detailed.feedback;

import android.support.annotation.Nullable;

import org.schulcloud.mobile.ui.base.MvpView;

/**
 * Date: 5/4/2018
 */
public interface FeedbackMvpView extends MvpView {

    void showGrade(@Nullable Integer grade, @Nullable String gradeComment);

}
