package org.schulcloud.mobile.ui.news.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.MvpView;

public interface DetailedNewsMvpView extends MvpView {

    void showNews(@NonNull News news);

}
