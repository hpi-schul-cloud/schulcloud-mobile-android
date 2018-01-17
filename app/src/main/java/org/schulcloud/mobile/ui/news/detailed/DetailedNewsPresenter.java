package org.schulcloud.mobile.ui.news.detailed;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

public class DetailedNewsPresenter extends BasePresenter<DetailedNewsMvpView> {

    private DataManager mDataManager;

    @Inject
    public DetailedNewsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void loadNews(@NonNull String newsId) {
        getViewOrThrow().showNews(mDataManager.getNewsForId(newsId));
    }
}
