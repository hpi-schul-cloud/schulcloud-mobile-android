package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.NewsDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.News;
import org.schulcloud.mobile.data.remote.RestService;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by araknor on 01.12.17.
 */

@Singleton
public class NewsDataManager {

    private final RestService mRestService;
    private final NewsDatabaseHelper mNewsDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public NewsDataManager(RestService restService, PreferencesHelper preferencesHelper,
                               NewsDatabaseHelper newsDatabaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mNewsDatabaseHelper = newsDatabaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<List<News>> getNews() {
        return mNewsDatabaseHelper.getNews();
    }
    public News getNewsForId(String newsId) {
        return mNewsDatabaseHelper.getNewsForId(newsId);
    }
    public Observable<News> syncNews() {
        return mRestService.getNews(userDataManager.getAccessToken())
                .concatMap(newsFeathersResponse -> {
                    mNewsDatabaseHelper.clearTable(News.class);
                    return mNewsDatabaseHelper.setNews(newsFeathersResponse.data);
                }).doOnError(Throwable::printStackTrace);
    }
}
