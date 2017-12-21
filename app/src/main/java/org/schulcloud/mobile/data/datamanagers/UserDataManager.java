package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.UserDatabaseHelper;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.crypt.JWTUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;

@Singleton
public class UserDataManager{

    private final RestService mRestService;
    private final UserDatabaseHelper mUserDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;

    @Inject
    public UserDataManager(RestService restService, PreferencesHelper preferencesHelper,
                       UserDatabaseHelper userDatabaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mUserDatabaseHelper = userDatabaseHelper;
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<User> syncUsers() {
        return mRestService.getUsers(getAccessToken())
                .concatMap(new Func1<List<User>, Observable<User>>() {
                    @Override
                    public Observable<User> call(List<User> users) {
                        return mUserDatabaseHelper.setUsers(users);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Observable<List<User>> getUsers() {
        return mUserDatabaseHelper.getUsers().distinct();
    }

    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    public Observable<CurrentUser> signIn(String username, String password) {
        return mRestService.signIn(new Credentials(username, password))
                .concatMap(accessToken -> {
                    // save current user data
                    String jwt = mPreferencesHelper.saveAccessToken(accessToken);
                    String currentUser = JWTUtil.decodeToCurrentUser(jwt);
                    mPreferencesHelper.saveCurrentUserId(currentUser);

                    return syncCurrentUser(currentUser);
                });
    }
    public void signOut() {
        mUserDatabaseHelper.clearAll();
        mPreferencesHelper.clear();
    }

    public Observable<CurrentUser> syncCurrentUser(String userId) {
        return mRestService.getUser(getAccessToken(), userId).concatMap(
                new Func1<CurrentUser, Observable<CurrentUser>>() {
                    @Override
                    public Observable<CurrentUser> call(CurrentUser currentUser) {
                        mPreferencesHelper.saveCurrentUsername(currentUser.displayName);
                        mPreferencesHelper.saveCurrentSchoolId(currentUser.schoolId);
                        return mUserDatabaseHelper.setCurrentUser(currentUser);
                    }
                }).doOnError(Throwable::printStackTrace);
    }

    public Single<CurrentUser> getCurrentUser() {
        return mUserDatabaseHelper.getCurrentUser();
    }

    public String getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }

    public void setInDemoMode(boolean isInDemoMode) {
        mPreferencesHelper.saveIsInDemoMode(isInDemoMode);
    }
    public Single<Boolean> isInDemoMode() {
        return Single.just(mPreferencesHelper.isInDemoMode());
    }
}
