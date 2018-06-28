package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.responseBodies.FeedbackResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.ui.PasswordRecovery.PasswordRecoveryMvpView;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

@Singleton
public class FeedbackDataManager {
    private final RestService mRestService;

    @Inject
    PreferencesHelper mPreferencesHelper;
    @Inject
    UserDataManager userDataManager;

    @Inject
    public FeedbackDataManager(RestService restService, PreferencesHelper preferencesHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<FeedbackResponse> sendFeedback(FeedbackRequest feedbackRequest) {
        return mRestService.sendFeedback(
                userDataManager.getAccessToken(),
                feedbackRequest)
                .concatMap(new Func1<FeedbackResponse, Observable<FeedbackResponse>>() {
                    @Override
                    public Observable<FeedbackResponse> call(FeedbackResponse feedbackResponse) {
                        return Observable.just(feedbackResponse);
                    }
                });
    }

    public void sendEmail(FeedbackRequest feedbackRequest){

                 mRestService
                .sendFeedback(userDataManager.getAccessToken(),feedbackRequest)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(res->{},error -> {});

    }
}
