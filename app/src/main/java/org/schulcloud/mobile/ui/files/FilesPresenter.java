package org.schulcloud.mobile.ui.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.CreateDirectoryRequest;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.PathUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
@ConfigPersistent
public class FilesPresenter extends BasePresenter<FilesMvpView> {

    private final UserDataManager mUserDataManager;
    private final FileDataManager mFileDataManager;
    private final CourseDataManager mCourseDataManager;
    private Subscription mCurrentUserSubscription;
    private Subscription mFileSubscription;
    private Subscription mFileDownloadGetterSubscription;
    private Subscription mFileDownloadSubscription;
    private Subscription mFileUploadGetterSubscription;
    private Subscription mFileUploadSubscription;
    private Subscription mFileDeleteSubscription;

    private Subscription mDirectorySubscription;
    private Subscription mDirectoryCreateSubscription;
    private Subscription mDirectoryDeleteSubscription;

    private String mFileToOpen = null;

    @Inject
    FilesPresenter(FileDataManager fileDataManager, UserDataManager userDataManager,
            CourseDataManager courseDataManager) {
        mFileDataManager = fileDataManager;
        mUserDataManager = userDataManager;
        mCourseDataManager = courseDataManager;

        loadFiles();
        loadDirectories();
    }

    @Override
    public void onViewAttached(@NonNull FilesMvpView view) {
        super.onViewAttached(view);
        loadPermissions();
        loadBreadcrumbs();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtil.unsubscribe(mCurrentUserSubscription);
        RxUtil.unsubscribe(mFileSubscription);
        RxUtil.unsubscribe(mFileDownloadGetterSubscription);
        RxUtil.unsubscribe(mFileDownloadSubscription);
        RxUtil.unsubscribe(mFileUploadGetterSubscription);
        RxUtil.unsubscribe(mFileUploadSubscription);
        RxUtil.unsubscribe(mFileDeleteSubscription);

        RxUtil.unsubscribe(mDirectorySubscription);
        RxUtil.unsubscribe(mDirectoryCreateSubscription);
        RxUtil.unsubscribe(mDirectoryDeleteSubscription);
    }

    private void loadPermissions() {
        RxUtil.unsubscribe(mCurrentUserSubscription);
        mCurrentUserSubscription = mUserDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentUser -> sendToView(v -> {
                    v.showCanUploadFile(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FILE_CREATE));
                    v.showCanDeleteFiles(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FILE_DELETE));
                    v.showCanCreateDirectories(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FOLDER_CREATE));
                    v.showCanDeleteDirectories(currentUser.hasPermission(
                            CurrentUser.PERMISSION_FOLDER_DELETE));
                }));
    }
    private void loadBreadcrumbs() {
        String path = mFileDataManager.getStorageContext();
        String courseId = mFileDataManager.isStorageContextCourse();
        if (mFileDataManager.isStorageContextMy())
            sendToView(view -> view.showBreadcrumbs(path, null));
        else if (courseId != null)
            sendToView(view ->
                    view.showBreadcrumbs(path, mCourseDataManager.getCourseForId(courseId)));
    }

    private void loadFiles() {
        RxUtil.unsubscribe(mFileSubscription);
        mFileSubscription = mFileDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        files -> {
                            sendToView(view -> view.showFiles(files));

                            if (mFileToOpen == null)
                                return;
                            for (File file : files)
                                if (file.name.equals(mFileToOpen)) {
                                    onFileSelected(file);
                                    mFileToOpen = null;
                                    return;
                                }

                            sendToView(v -> v.showFileError_notFound(mFileToOpen));
                            mFileToOpen = null;
                        },
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            sendToView(FilesMvpView::showFilesLoadError);
                        });
    }

    /* File download */
    public void onFileSelected(@NonNull File file) {
        loadFileFromServer(file, false);
    }
    public void onFileDownloadSelected(@NonNull File file) {
        loadFileFromServer(file, true);
    }
    /**
     * Loads a file from the Schul-Cloud server.
     *
     * @param file     {File} - the db-saved file
     * @param download {Boolean} - whether to download the file or not
     */
    private void loadFileFromServer(@NonNull File file, boolean download) {
        sendToView(FilesMvpView::showFileDownloadStarted);
        RxUtil.unsubscribe(mFileDownloadGetterSubscription);
        mFileDownloadGetterSubscription = mFileDataManager
                .getFileUrl(new SignedUrlRequest(
                        SignedUrlRequest.ACTION_GET,
                        file.key,
                        file.type))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> {
                            Log.d("Fetched file url", signedUrlResponse.url);
                            if (download)
                                downloadFile(signedUrlResponse.url, file.name);
                            else
                                sendToView(view -> view.showFile(
                                        signedUrlResponse.url,
                                        signedUrlResponse.header.getContentType(),
                                        FileUtils.getExtension(file.name)));
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FilesMvpView::showFileDownloadError);
                        });
    }
    /**
     * Downloads the file from the given url.
     *
     * @param url      The remote url from which the file will be downloaded
     * @param fileName The name of the downloaded file
     */
    private void downloadFile(@NonNull String url, @NonNull String fileName) {
        RxUtil.unsubscribe(mFileDownloadSubscription);
        mFileDownloadSubscription = mFileDataManager
                .downloadFile(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> view.saveFile(fileName, responseBody)),
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FilesMvpView::showFileDownloadError);
                        });
    }

    /* File upload */
    public void onFileUploadSelected(@NonNull java.io.File file) {
        sendToView(FilesMvpView::showFileUploadStarted);
        String uploadPath = PathUtil
                .combine(mFileDataManager.getStorageContext(), file.getName());

        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(
                SignedUrlRequest.ACTION_PUT,
                uploadPath,
                FileUtils.getMimeType(file));

        RxUtil.unsubscribe(mFileUploadGetterSubscription);
        mFileUploadGetterSubscription = mFileDataManager.getFileUrl(signedUrlRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> uploadFile(file, signedUrlResponse),
                        error -> {
                            Timber.e(error, "There was an error uploading file to Server.");
                            sendToView(FilesMvpView::showFileUploadError);
                        });
    }
    /**
     * Uploads the file to the given url.
     *
     * @param file              The file to upload
     * @param signedUrlResponse The remote url to which the file will be uploaded
     */
    private void uploadFile(@NonNull java.io.File file,
            @NonNull SignedUrlResponse signedUrlResponse) {
        RxUtil.unsubscribe(mFileUploadSubscription);
        mFileUploadSubscription = mFileDataManager.uploadFile(file, signedUrlResponse)
                .flatMap(responseBody -> mFileDataManager
                        .persistFile(signedUrlResponse, file.getName(),
                                FileUtils.getMimeType(file), file.length()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> {
                            view.reloadFiles();
                            view.showFileUploadSuccess();
                        }),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            sendToView(FilesMvpView::showFileUploadError);
                        });

    }

    /* File deletion */
    /**
     * Deletes a file from the server.
     *
     * @param file The file to delete
     */
    public void onFileDeleteSelected(@NonNull File file) {
        RxUtil.unsubscribe(mFileDeleteSubscription);
        mFileDeleteSubscription = mFileDataManager.deleteFile(file.key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> {
                            view.reloadFiles();
                            view.showFileDeleteSuccess();
                        }),
                        error -> {
                            Timber.e(error, "There was an error deleting file from Server.");
                            sendToView(FilesMvpView::showFileDeleteError);
                        });

    }


    private void loadDirectories() {
        RxUtil.unsubscribe(mDirectorySubscription);
        mDirectorySubscription = mFileDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        directories -> sendToView(view -> view.showDirectories(directories)),
                        error -> {
                            Timber.e(error, "There was an error loading the directories.");
                            sendToView(FilesMvpView::showDirectoriesLoadError);
                        });
    }
    /* Directory change */
    /**
     * Opens a directory by fetching files for new storageContext.
     *
     * @param directory The selected directory
     */
    public void onDirectorySelected(@NonNull Directory directory) {
        onDirectorySelected(PathUtil.combine(directory.path, directory.name), null);
    }
    public void onDirectorySelected(@Nullable String path, @Nullable String file) {
        if (path == null)
            return;

        mFileToOpen = file;

        mFileDataManager.setStorageContext(path);
        loadBreadcrumbs();
        sendToView(view -> {
            view.reloadFiles();
            view.reloadDirectories();
        });
    }

    /* Directory creation */
    public void onDirectoryCreateSelected(@NonNull String name) {
        RxUtil.unsubscribe(mDirectoryCreateSubscription);
        mDirectoryCreateSubscription = mFileDataManager
                .createDirectory(new CreateDirectoryRequest(
                        PathUtil.combine(mFileDataManager.getStorageContext(), name)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        directory -> sendToView(view -> {
                            view.reloadDirectories();
                            view.showDirectoryCreateSuccess();
                        }),
                        error -> {
                            Timber.e(error,
                                    "There was an error while creating a directory on the Server.");
                            sendToView(FilesMvpView::showDirectoryCreateError);
                        });
    }

    /* Directory deletion */
    /**
     * Deletes a directory from the server.
     *
     * @param directory The directory to delete
     */
    public void onDirectoryDeleteSelected(@NonNull Directory directory) {
        RxUtil.unsubscribe(mDirectoryDeleteSubscription);
        mDirectoryDeleteSubscription = mFileDataManager
                .deleteDirectory(PathUtil.combine(directory.path, directory.name))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            sendToView(FilesMvpView::showDirectoryDeleteSuccess);
                            sendToView(FilesMvpView::reloadDirectories);
                        },
                        error -> {
                            Timber.e(error, "There was an error deleting directory from Server.");
                            sendToView(FilesMvpView::showDirectoryDeleteError);
                        });
    }

    /**
     * Checks the current storage path and steps back if it's not a root directory.
     *
     * @return True if stepping back was successful, false if we are already in the root directory.
     */
    public boolean onBackSelected() {
        String storageContext = mFileDataManager.getStorageContext();

        // first two parts are meta
        if (storageContext.split("/", 4).length > 3) {
            onDirectorySelected(PathUtil.parent(storageContext), null);
            return true;
        }
        return false;
    }
}
