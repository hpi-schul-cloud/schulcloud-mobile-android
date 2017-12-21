package org.schulcloud.mobile.ui.news.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    private NewsDataManager mNewsDataManager;

    @Inject
    public DetailedNewsPresenter(NewsDataManager newsDataManager) {
        mNewsDataManager = newsDataManager;
    }

    public void loadNews(@NonNull String newsId) {
        getViewOrThrow().showNews(mNewsDataManager.getNewsForId(newsId));
    }
}
