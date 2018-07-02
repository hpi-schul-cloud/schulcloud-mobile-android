package org.schulcloud.mobile.data.datamanagers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.schulcloud.mobile.data.local.FileStorageDatabaseHelper;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.CreateDirectoryRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.data.remote.RestService;
import org.schulcloud.mobile.util.PathUtil;
import org.schulcloud.mobile.util.WebUtil;

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
    private static final String TAG = FileDataManager.class.getSimpleName();

    private final RestService mRestService;
    private final FileStorageDatabaseHelper mDatabaseHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final UserDataManager mUserDataManager;

    public static final String CONTEXT_MY = WebUtil.PATH_INTERNAL_FILES_MY;
    private static final String CONTEXT_MY_API = "users";
    public static final String CONTEXT_COURSES = "courses";

    @Inject
    public FileDataManager(RestService restService, PreferencesHelper preferencesHelper,
            FileStorageDatabaseHelper databaseHelper, UserDataManager userDataManager) {
        mRestService = restService;
        mPreferencesHelper = preferencesHelper;
        mDatabaseHelper = databaseHelper;
        mUserDataManager = userDataManager;
    }

    // Files
    @NonNull
    public Observable<File> syncFiles(@NonNull String path) {
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
                }).doOnError(throwable -> Log.w(TAG, "Error syncing files", throwable));
    }
    @NonNull
    public Observable<List<File>> getFiles() {
        return mDatabaseHelper.getFiles()
                .map(files -> {
                    List<File> filteredFiles = new ArrayList<>();
                    String currentContext = PathUtil.trimTrailingSlash(getStorageContext());
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
    public Observable<SignedUrlResponse> getFileUrl(@NonNull SignedUrlRequest signedUrlRequest) {
        return mRestService.generateSignedUrl(mUserDataManager.getAccessToken(), signedUrlRequest);
    }
    @NonNull
    public Observable<ResponseBody> downloadFile(@NonNull String url) {
        return mRestService.downloadFile(url);
    }

    @NonNull
    public Observable<ResponseBody> uploadFile(@NonNull java.io.File file,
            @NonNull SignedUrlResponse signedUrlResponse) {
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

    @NonNull
    public Observable<ResponseBody> deleteFile(@NonNull String path) {
        return mRestService.deleteFile(mUserDataManager.getAccessToken(), path);
    }

    // Directories
    @NonNull
    public Observable<Directory> syncDirectories(@NonNull String path) {
        return mRestService.getFiles(mUserDataManager.getAccessToken(), path)
                .concatMap(filesResponse -> {
                    // clear old directories
                    mDatabaseHelper.clearTable(Directory.class);

                    List<Directory> improvedDirs = new ArrayList<>();
                    for (Directory d : filesResponse.directories) {
                        d.path = getStorageContext();
                        improvedDirs.add(d);
                    }

                    return mDatabaseHelper.setDirectories(improvedDirs);
                }).doOnError(throwable -> Log.w(TAG, "Error syncing directories", throwable));
    }
    @NonNull
    public Observable<List<Directory>> getDirectories() {
        return mDatabaseHelper.getDirectories()
                .map(directories -> {
                    List<Directory> filteredDirectories = new ArrayList<>();
                    String currentContext = getStorageContext();
                    for (Directory d : directories)
                        if (d.path.equals(currentContext))
                            filteredDirectories.add(d);

                    Collections.sort(filteredDirectories, (o1, o2) ->
                            o1.name == null ? (o2.name == null ? 0 : -1)
                                    : o1.name.compareTo(o2.name));
                    return filteredDirectories;
                });
    }

    @NonNull
    public Observable<Directory> createDirectory(
            @NonNull CreateDirectoryRequest createDirectoryRequest) {
        return mRestService
                .createDirectory(mUserDataManager.getAccessToken(), createDirectoryRequest);
    }

    @NonNull
    public Observable<ResponseBody> deleteDirectory(@NonNull String path) {
        return mRestService.deleteDirectory(mUserDataManager.getAccessToken(), path);
    }

    // Storage context
    @NonNull
    public String getStorageContext() {
        String storageContext = mPreferencesHelper.getCurrentStorageContext();
        // personal files are default
        return PathUtil.ensureTrailingSlash(storageContext.equals("null")
                ? PathUtil.combine("users", mUserDataManager.getCurrentUserId())
                : storageContext);
    }
    public void setStorageContext(@NonNull String storageContext) {
        storageContext = PathUtil.trimSlashes(storageContext);
        if (storageContext.startsWith(CONTEXT_MY))
            storageContext = PathUtil.combine(CONTEXT_MY_API, mUserDataManager.getCurrentUserId(),
                    storageContext.substring(CONTEXT_MY.length()));
        mPreferencesHelper.saveCurrentStorageContext(storageContext);
    }
    public void setStorageContextToRoot() {
        setStorageContext("");
    }
    public boolean isStorageContextMy() {
        return getStorageContext().startsWith(CONTEXT_MY_API);
    }
    public void setStorageContextToMy() {
        setStorageContext(CONTEXT_MY);
    }
    @Nullable
    public String isStorageContextCourse() {
        String storageContext = getStorageContext();
        if (!storageContext.startsWith(CONTEXT_COURSES))
            return null;
        return PathUtil.getAllParts(storageContext, 3)[1];
    }
    public void setCurrentStorageContextToCourse(@NonNull String courseId) {
        setStorageContext(PathUtil.combine(CONTEXT_COURSES, courseId));
    }
}
