package org.schulcloud.mobile.data.datamanagers;

import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.local.UserDatabaseHelper;
import org.schulcloud.mobile.data.model.CurrentAccount;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.AccountRequest;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.requestBodies.UserRequest;
import org.schulcloud.mobile.data.model.responseBodies.AccountResponse;
import org.schulcloud.mobile.data.model.responseBodies.ProfileResponse;
import org.schulcloud.mobile.data.model.responseBodies.UserResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.crypt.JWTUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.functions.Func2;

@Singleton
public class UserDataManager{

    private final RestService mRestService;
    private final UserDatabaseHelper mUserDatabaseHelper;

    @Inject
    PreferencesHelper mPreferencesHelper;

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    @Inject
    public UserDataManager(RestService restService, PreferencesHelper preferencesHelper,
                       UserDatabaseHelper userDatabaseHelper) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mUserDatabaseHelper = userDatabaseHelper;
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
        return mUserDatabaseHelper.getUsers().distinctUntilChanged();
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

    public String getCurrentUserName() {
        return mPreferencesHelper.getCurrentUsername();
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

    public Observable<UserResponse> changeUserInfo(UserRequest userRequest) {
        return mRestService.changeUserInfo(
                getAccessToken(),
                userRequest)
                .concatMap(new Func1<UserResponse, Observable<UserResponse>>() {
                    @Override
                    public Observable<UserResponse> call(UserResponse userResponse) {
                        return Observable.just(userResponse);
                    }
                });
    }

    public Observable<AccountResponse> changeAccountInfo(AccountRequest accountRequest) {
        return mRestService.changeAccountInfo(
                getAccessToken(),
                accountRequest)
                .concatMap(new Func1<AccountResponse, Observable<? extends AccountResponse>>() {
                    @Override
                    public Observable<AccountResponse> call(AccountResponse accountResponse) {
                        return Observable.just(accountResponse);
                    }
                });
    }

    public Observable<ProfileResponse> changeProfileInfo(AccountRequest accountRequest,
                                                         UserRequest userRequest) {
        Observable<AccountResponse> accountResponseObservable = changeAccountInfo(accountRequest);
        Observable<UserResponse> userResponseObservable = changeUserInfo(userRequest);
        Observable<ProfileResponse> profileResponseObservable = Observable
                .zip(accountResponseObservable, userResponseObservable, new Func2<AccountResponse, UserResponse, ProfileResponse>() {
                    @Override
                    public ProfileResponse call(AccountResponse accountResponse, UserResponse userResponse) {
                        return new ProfileResponse(userResponse, accountResponse);
                    }
                });
        return profileResponseObservable;
    }

    public Single<CurrentAccount> getCurrentAccount(){
        return mUserDatabaseHelper.getCurrentAccount();
    }
}
