package org.schulcloud.mobile.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.Credentials;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.responseBodies.FilesResponse;
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

    /**** User ****/

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

    /**** Files ****/

    public Observable<File> syncFiles(String storageContext) {
        // todo: get AccessToken from DB
        // todo: also fetch directories
        return mRestService.getFiles(
                "eyJhbGciOiJIUzI1NiIsInR5cCI6ImFjY2VzcyJ9.eyJhY2NvdW50SWQiOiIwMDAwZDIxMzgxNmFiYmE1ODQ3MTRjYWEiLCJ1c2VySWQiOiIwMDAwZDIxMzgxNmFiYmE1ODQ3MTRjMGEiLCJpYXQiOjE0OTI3NjA2MDMsImV4cCI6MTQ5Mjg0NzAwMywiYXVkIjoiaHR0cHM6Ly95b3VyZG9tYWluLmNvbSIsImlzcyI6ImZlYXRoZXJzIiwic3ViIjoiYW5vbnltb3VzIn0.tP3UlFYAptKQhKANJ2BU1ZvLO2OoTalnamV1HwYWEyE",
                storageContext)
                .concatMap(new Func1<FilesResponse, Observable<File>>() {
                    @Override
                    public Observable<File> call(FilesResponse filesResponse) {
                        return mDatabaseHelper.setFiles(filesResponse.files);
                    }
                });
    }

    public Observable<List<File>> getFiles() {
        return mDatabaseHelper.getFiles().distinct();
    }

}
