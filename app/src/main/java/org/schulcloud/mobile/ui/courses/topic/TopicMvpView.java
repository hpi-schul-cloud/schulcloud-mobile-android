package org.schulcloud.mobile.ui.courses.topic;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface TopicMvpView extends MvpView {

    void showName(@NonNull String name);
    void showContent(@NonNull List<Contents> contents);

}
