package org.schulcloud.mobile.data.datamanagers;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.local.FileStorageDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.CreateDirectoryRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.PathUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

@Singleton
public class FileDataManager {
    private final RestService mRestService;
    private final FileStorageDatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final UserDataManager mUserDataManager;

    public static final String CONTEXT_MY = "users";
    public static final String CONTEXT_COURSES = "courses";

    @Inject
    public FileDataManager(RestService restService, PreferencesHelper preferencesHelper,
            FileStorageDatabaseHelper databaseHelper, UserDataManager userDataManager) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mUserDataManager = userDataManager;
    }

    @NonNull
    public Observable<File> syncFiles(String path) {
        return mRestService.getFiles(mUserDataManager.getAccessToken(), path)
                .concatMap(filesResponse -> {
                    // clear old files
                    mDatabaseHelper.clearTable(File.class);

                    List<File> files = new ArrayList<>();

                    // set fullPath for every file
                    for (File file : filesResponse.files) {
                        file.fullPath =
                                file.key.substring(0, file.key.lastIndexOf(java.io.File.separator));
                        files.add(file);
                    }

                    return mDatabaseHelper.setFiles(files);
                }).doOnError(Throwable::printStackTrace);
    }
    @NonNull
    public Observable<List<File>> getFiles() {
        return mDatabaseHelper.getFiles()
                .map(files -> {
                    List<File> filteredFiles = new ArrayList<>();
                    String currentContext = PathUtil.trimTrailingSlash(getCurrentStorageContext());
                    for (File f : files)
                        if (f.fullPath.equals(currentContext))
                            filteredFiles.add(f);

                    Collections.sort(filteredFiles, (o1, o2) ->
                            o1.name == null ? (o2.name == null ? 0 : -1)
                                    : o1.name.compareTo(o2.name));
                    return filteredFiles;
                });
    }

    @NonNull
    public Observable<Directory> syncDirectories(String path) {
        return mRestService.getFiles(mUserDataManager.getAccessToken(), path)
                .concatMap(filesResponse -> {
                    // clear old directories
                    mDatabaseHelper.clearTable(Directory.class);

                    List<Directory> improvedDirs = new ArrayList<>();
                    for (Directory d : filesResponse.directories) {
                        d.path = getCurrentStorageContext();
                        improvedDirs.add(d);
                    }

                    return mDatabaseHelper.setDirectories(improvedDirs);
                }).doOnError(Throwable::printStackTrace);
    }
    @NonNull
    public Observable<List<Directory>> getDirectories() {
        return mDatabaseHelper.getDirectories()
                .map(directories -> {
                    List<Directory> filteredDirectories = new ArrayList<>();
                    String currentContext = getCurrentStorageContext();
                    for (Directory d : directories)
                        if (d.path.equals(currentContext))
                            filteredDirectories.add(d);

                    Collections.sort(filteredDirectories, (o1, o2) ->
                            o1.name == null ? (o2.name == null ? 0 : -1)
                                    : o1.name.compareTo(o2.name));
                    return filteredDirectories;
                });
    }

    public Observable<ResponseBody> deleteDirectory(String path) {
        return mRestService.deleteDirectory(mUserDataManager.getAccessToken(), path);
    }

    public Observable<SignedUrlResponse> getFileUrl(SignedUrlRequest signedUrlRequest) {
        return mRestService.generateSignedUrl(mUserDataManager.getAccessToken(), signedUrlRequest);
    }

    public Observable<ResponseBody> downloadFile(String url) {
        return mRestService.downloadFile(url);
    }

    public Observable<ResponseBody> uploadFile(java.io.File file,
            SignedUrlResponse signedUrlResponse) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("file/*"), file);
        return mRestService.uploadFile(
                signedUrlResponse.url,
                signedUrlResponse.header.getContentType(),
                signedUrlResponse.header.getMetaPath(),
                signedUrlResponse.header.getMetaName(),
                signedUrlResponse.header.getMetaFlatName(),
                signedUrlResponse.header.getMetaThumbnail(),
                requestBody
        );
    }

    public Observable<ResponseBody> deleteFile(String path) {
        return mRestService.deleteFile(mUserDataManager.getAccessToken(), path);
    }

    public String getCurrentStorageContext() {
        String storageContext = mPreferencesHelper.getCurrentStorageContext();
        // personal files are default
        return storageContext.equals("null") ? "users/" + mUserDataManager.getCurrentUserId() + "/"
                : storageContext + "/";
    }

    public void setCurrentStorageContext(String newStorageContext) {
        mPreferencesHelper.saveCurrentStorageContext(newStorageContext);
    }

    @NonNull
    public Observable<ResponseBody> persistFile(@NonNull SignedUrlResponse signedUrl,
            @NonNull String fileName, @NonNull String fileType, long fileSize) {
        File newFile = new File();
        newFile.key = PathUtil.combine(signedUrl.header.getMetaPath(), fileName);
        newFile.path = PathUtil.ensureTrailingSlash(signedUrl.header.getMetaPath());
        newFile.name = fileName;
        newFile.type = fileType;
        newFile.size = "" + fileSize;
        newFile.flatFileName = signedUrl.header.getMetaFlatName();
        newFile.thumbnail = signedUrl.header.getMetaThumbnail();
        return mRestService.persistFile(mUserDataManager.getAccessToken(), newFile);
    }

    public void setCurrentStorageContextToRoot() {
        setCurrentStorageContext("");
    }
    public void setCurrentStorageContextToMy() {
        setCurrentStorageContext(PathUtil.combine(CONTEXT_MY, mUserDataManager.getCurrentUserId()));
    }
    public void setCurrentStorageContextToCourse(@NonNull String courseId) {
        setCurrentStorageContext(PathUtil.combine(CONTEXT_COURSES, courseId));
    }

    @NonNull
    public Observable<Directory> createDirectory(
            @NonNull CreateDirectoryRequest createDirectoryRequest) {
        return mRestService
                .createDirectory(mUserDataManager.getAccessToken(), createDirectoryRequest);
    }
}
