package org.schulcloud.mobile.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Credentials;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.remote.RestService;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class DataManager {

    private final RestService mRestService;
    private final DatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public DataManager(RestService restService, PreferencesHelper preferencesHelper,
                       DatabaseHelper databaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<User> syncUsers() {
        return mRestService.getUsers()
                .concatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        return mDatabaseHelper.setUsers(users);
                    }
                });
    }

    public Observable<List<User>> getUsers() {
        return mDatabaseHelper.getUsers().distinct();
    }

    public Observable<AccessToken> getAccessToken() {
        return mDatabaseHelper.getAccessToken().distinct();
    }

    public Observable<AccessToken> signIn(String username, String password) {
        return mRestService.signIn(new Credentials(username, password))
                .concatMap(new Func1<AccessToken, Observable<AccessToken>>() {
                    @Override
                    public Observable<AccessToken> call(AccessToken accessToken) {
                        return mDatabaseHelper.setAccessToken(accessToken)
                                .concatMap(new Func1<AccessToken, Observable<AccessToken>>() {
                                    @Override
                                    public Observable<AccessToken> call(AccessToken accessToken) {
                                        return mDatabaseHelper.getAccessToken();
                                    }
                                });
                    }
                });
    }
}
