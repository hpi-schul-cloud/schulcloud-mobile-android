package org.schulcloud.mobile.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.schulcloud.mobile.data.local.DatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Directory;
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

    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    public Observable<String> signIn(String username, String password) {
        return mRestService.signIn(new Credentials(username, password))
                .concatMap(new Func1<AccessToken, Observable<String>>() {
                    @Override
                    public Observable<String> call(AccessToken accessToken) {
                        return Observable.just(mPreferencesHelper.saveAccessToken(accessToken));
                    }
                });
    }

    /**** Files ****/

    public Observable<File> syncFiles(String storageContext) {
        // get AccessToken for Authorization
        return mRestService.getFiles(
                getAccessToken(),
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

    public Observable<Directory> syncDirectories(String storageContext) {
        // get AccessToken for Authorization
        return mRestService.getFiles(
                getAccessToken(),
                storageContext)
                .concatMap(new Func1<FilesResponse, Observable<Directory>>() {
                    @Override
                    public Observable<Directory> call(FilesResponse filesResponse) {
                        return mDatabaseHelper.setDirectories(filesResponse.directories);
                    }
                });
    }

    public Observable<List<Directory>> getDirectories() {
        return mDatabaseHelper.getDirectories().distinct();
    }

}
