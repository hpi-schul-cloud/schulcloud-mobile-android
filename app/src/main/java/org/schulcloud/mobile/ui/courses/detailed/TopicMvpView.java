package org.schulcloud.mobile.ui.courses.detailed;

import org.schulcloud.mobile.data.model.Contents;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface TopicMvpView extends MvpView {

    void showContent(List<Contents> contents);

    void showError();
}
