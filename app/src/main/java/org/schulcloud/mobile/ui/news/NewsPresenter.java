package org.schulcloud.mobile.ui.news;

import android.support.annotation.NonNull;
import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class NewsPresenter extends BasePresenter<NewsMvpView> {

    private NewsDataManager mNewsDataManager;
    private Subscription mSubscription;

    @Inject
    public NewsPresenter(NewsDataManager newsDataManager) {
        mNewsDataManager = newsDataManager;
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
    }

    public void loadNews() {
        RxUtil.unsubscribe(mSubscription);
        mSubscription = mNewsDataManager.getNews()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        news -> {
                            if (news.isEmpty())
                                sendToView(NewsMvpView::showNewsEmpty);
                            else
                                sendToView(view -> view.showNews(news));
                        },
                        throwable -> {
                            Timber.e(throwable, "An error occured while loading news");
                            sendToView(NewsMvpView::showError);
                        });
    }

    public void showNewsDetail(@NonNull String newsId) {
        getViewOrThrow().showNewsDetail(newsId);
    }
}
