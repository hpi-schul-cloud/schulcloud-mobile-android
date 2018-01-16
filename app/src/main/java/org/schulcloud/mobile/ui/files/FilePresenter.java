package org.schulcloud.mobile.ui.files;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class FilePresenter extends BasePresenter<FileMvpView> {

    private DataManager mDataManager;
    private Subscription fileSubscription;
    private Subscription directorySubscription;
    private Subscription fileGetterSubscription;
    private Subscription fileDownloadSubscription;
    private Subscription fileUploadSubscription;
    private Subscription fileStartUploadSubscription;
    private Subscription fileDeleteSubscription;
    private Subscription directoryDeleteSubscription;

    @Inject
    FilePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void onViewAttached(@NonNull FileMvpView view) {
        super.onViewAttached(view);
        mDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentUser -> {
                    sendToView(v -> v.showCanCreateFile(
                            currentUser.hasPermission(CurrentUser.PERMISSION_FILE_CREATE)));
                    sendToView(v -> v.showCanDeleteFiles(
                            currentUser.hasPermission(CurrentUser.PERMISSION_FILE_DELETE)));
                    sendToView(v -> v.showCanDeleteDirectories(
                            currentUser.hasPermission(CurrentUser.PERMISSION_FOLDER_DELETE)));
                });
    }
    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(fileSubscription);
        RxUtil.unsubscribe(directorySubscription);
        RxUtil.unsubscribe(fileGetterSubscription);
        RxUtil.unsubscribe(fileDownloadSubscription);
        RxUtil.unsubscribe(fileUploadSubscription);
        RxUtil.unsubscribe(fileStartUploadSubscription);
        RxUtil.unsubscribe(fileDeleteSubscription);
        RxUtil.unsubscribe(directoryDeleteSubscription);
    }

    public void loadFiles() {
        RxUtil.unsubscribe(fileSubscription);
        fileSubscription = mDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        files -> sendToView(view -> view.showFiles(files)),
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            sendToView(FileMvpView::showError);
                        });
    }
    public void loadDirectories() {
        RxUtil.unsubscribe(directorySubscription);
        directorySubscription = mDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        directories -> sendToView(view -> view.showDirectories(directories)),
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the directories.");
                            sendToView(FileMvpView::showError);
                        });
    }

    /**
     * Loads a file from the Schul-Cloud server.
     *
     * @param file     {File} - the db-saved file
     * @param download {Boolean} - whether to download the file or not
     */
    public void loadFileFromServer(@NonNull File file, @NonNull Boolean download) {
        RxUtil.unsubscribe(fileGetterSubscription);
        fileGetterSubscription = mDataManager
                .getFileUrl(new SignedUrlRequest(
                        SignedUrlRequest.ACTION_OBJECT_GET, // action
                        file.key, // path
                        file.type)) // fileType
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> {
                            Log.d("Fetched file url", signedUrlResponse.url);
                            if (download)
                                downloadFile(signedUrlResponse.url, file.name);
                            else
                                sendToView(view -> view.showFile(
                                        signedUrlResponse.url,
                                        signedUrlResponse.header.getContentType()));
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FileMvpView::showLoadingFileFromServerError);
                        });
    }

    /**
     * Downloads a file from a given url.
     *
     * @param url      {String} - the remote url from which the file will be downloaded
     * @param fileName {String} - the name of the downloaded file
     */
    public void downloadFile(@NonNull String url, @NonNull String fileName) {
        RxUtil.unsubscribe(fileDownloadSubscription);
        fileDownloadSubscription = mDataManager.downloadFile(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> view.saveFile(responseBody, fileName)),
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            sendToView(FileMvpView::showLoadingFileFromServerError);
                        });
    }


    /**
     * Opens a directory by fetching files for new storageContext.
     *
     * @param dirName {String} - the directory's name for which the files will be fetched
     */
    public void goIntoDirectory(@NonNull String dirName) {
        mDataManager.setCurrentStorageContext(dirName);
        getViewOrThrow().reloadFiles();
    }

    /**
     * Uploads a local file to the server.
     *
     * @param fileToUpload {File} - the file which will be uploaded
     */
    public void uploadFileToServer(@NonNull java.io.File fileToUpload) {
        // todo: refactor later on when there are class and course folders
        String uploadPath = mDataManager.getCurrentStorageContext() + fileToUpload.getName();

        SignedUrlRequest signedUrlRequest = new SignedUrlRequest(
                SignedUrlRequest.ACTION_OBJECT_PUT, // action
                uploadPath,
                FileUtils.getMimeType(fileToUpload));

        RxUtil.unsubscribe(fileUploadSubscription);
        fileUploadSubscription = mDataManager.getFileUrl(signedUrlRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        signedUrlResponse -> startUploading(fileToUpload, signedUrlResponse),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            sendToView(FileMvpView::showUploadFileError);
                        });
    }

    /**
     * Initiates a download task.
     *
     * @param file     {File} - the file which will be downloaded
     * @param download {Boolean} - whether to download on hard disk
     */
    public void startDownloading(@NonNull File file, boolean download) {
        getViewOrThrow().startDownloading(file, download);
    }
    /**
     * Starts an upload progress to the given url.
     *
     * @param file              {File} - the file which will be uploaded
     * @param signedUrlResponse {SignedUrlResponse} - contains information about the uploaded file
     */
    public void startUploading(@NonNull java.io.File file,
            @NonNull SignedUrlResponse signedUrlResponse) {
        RxUtil.unsubscribe(fileStartUploadSubscription);
        fileStartUploadSubscription = mDataManager.uploadFile(file, signedUrlResponse)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(FileMvpView::reloadFiles),
                        error -> {
                            Timber.e(error, "There was an error uploading file from Server.");
                            sendToView(FileMvpView::showUploadFileError);
                        });

    }

    public void startFileDeleting(@NonNull String path, @NonNull String fileName) {
        getViewOrThrow().startFileDeleting(path, fileName);
    }
    /**
     * Deletes a file from the server.
     *
     * @param path {String} - the key/path to the file
     */
    public void deleteFile(@NonNull String path) {
        RxUtil.unsubscribe(fileDeleteSubscription);
        fileDeleteSubscription = mDataManager.deleteFile(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(FileMvpView::showFileDeleteSuccess),
                        error -> {
                            Timber.e(error, "There was an error deleting file from Server.");
                            sendToView(FileMvpView::showFileDeleteError);
                        });

    }

    public void startDirectoryDeleting(String path, String dirName) {
        getViewOrThrow().startDirectoryDeleting(path, dirName);
    }
    /**
     * Deletes a directory from the server.
     *
     * @param path {String} - the key/path to the directory
     */
    public void deleteDirectory(@NonNull String path) {
        RxUtil.unsubscribe(directoryDeleteSubscription);
        directoryDeleteSubscription = mDataManager.deleteDirectory(path)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(FileMvpView::showDirectoryDeleteSuccess),
                        error -> {// TODO
                            Timber.e(error, "There was an error deleting file from Server.");
                            sendToView(FileMvpView::showDirectoryDeleteError);
                        });

    }

    /**
     * Checks the current storage path and steps back if it's not a root directory.
     *
     * @return True if stepping back was successful, false if we are already in the root directory.
     */
    boolean stepOneDirectoryBack() {
        String currentPath = mDataManager.getCurrentStorageContext();

        // remove last slash
        if (currentPath.lastIndexOf(java.io.File.separator) == (currentPath.length() - 1))
            currentPath = currentPath.substring(0, currentPath.length() - 1);

        // first two parts are meta
        if (currentPath.split(java.io.File.separator).length > 2) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf(java.io.File.separator));
            mDataManager.setCurrentStorageContext(currentPath);
            getViewOrThrow().reloadFiles();
            return true;
        }
        return false;
    }
}

