package org.schulcloud.mobile.ui.news;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

public interface NewsMvpView extends MvpView {

    void showNews(@NonNull List<News> news);

    void showNewsEmpty();

    void showError();

    void showNewsDetail(@NonNull String newsId);

}
