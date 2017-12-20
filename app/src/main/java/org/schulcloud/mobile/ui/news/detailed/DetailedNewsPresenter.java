package org.schulcloud.mobile.ui.news.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

@ConfigPersistent
public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    private final NewsDataManager mNewsDataManager;
    private News mNews;

    @Inject
    public DetailedNewsPresenter(NewsDataManager newsDataManager) {
        mNewsDataManager = newsDataManager;
    }

    @Override
    public void onViewAttached(@NonNull DetailedNewsMvpView view) {
        super.onViewAttached(view);
        showNews();
    }

    public void loadNews(@NonNull String newsId) {
        mNews = mNewsDataManager.getNewsForId(newsId);
        showNews();
    }
    private void showNews() {
        sendToView(v -> {
            if (mNews != null)
                v.showNews(mNews);
        });
    }
}
