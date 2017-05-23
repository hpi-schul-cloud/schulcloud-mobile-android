package org.schulcloud.mobile.ui.files;

import android.content.Context;
import android.util.Log;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.requestBodies.SignedUrlRequest;
import org.schulcloud.mobile.data.model.responseBodies.SignedUrlResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.FileSavingUtil;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@ConfigPersistent
public class FilePresenter extends BasePresenter<FileMvpView> {
    private Subscription fileSubscription;
    private Subscription directorySubscription;
    private Subscription fileGetterSubscription;
    private Subscription fileDownloadSubscription;

    private final String GET_OBJECT_ACTION = "getObject";

    @Inject
    public FilePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(FileMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (fileSubscription != null) fileSubscription.unsubscribe();
        if (directorySubscription != null) directorySubscription.unsubscribe();
        if (fileGetterSubscription != null) fileGetterSubscription.unsubscribe();
        if (fileDownloadSubscription != null) fileDownloadSubscription.unsubscribe();

    }

    public void loadFiles() {
        checkViewAttached();
        RxUtil.unsubscribe(fileSubscription);
        fileSubscription = mDataManager.getFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        files -> {
                            getMvpView().showFiles(files);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the files.");
                            getMvpView().showError();
                        },
                        () -> {
                        });
    }

    public void loadDirectories() {
        checkViewAttached();
        RxUtil.unsubscribe(directorySubscription);
        directorySubscription = mDataManager.getDirectories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        directories -> {
                            getMvpView().showDirectories(directories);
                        },
                        // onError
                        error -> {
                            Timber.e(error, "There was an error loading the directories.");
                            getMvpView().showError();
                        },
                        () -> {
                        });
    }

    /**
     * loads a file from the schul-cloud server
     * @param file {File} - the db-saved file
     * @param download {Boolean} - whether to download the file or not
     */
    public void loadFileFromServer(File file, Boolean download) {
        checkViewAttached();

        if (fileGetterSubscription != null && !fileGetterSubscription.isUnsubscribed())
            fileGetterSubscription.unsubscribe();

        fileGetterSubscription = mDataManager.getFileUrl(new SignedUrlRequest(
                this.GET_OBJECT_ACTION, // action
                file.key, // path
                file.type // fileType
        )).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (signedUrlResponse) -> {
                            Log.d("Fetched file url", signedUrlResponse.url);

                            if (download) {
                                downloadFile(signedUrlResponse.url, file.name);
                            } else {
                                getMvpView().showFile(
                                        signedUrlResponse.url,
                                        signedUrlResponse.header.getContentType());
                            }
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            getMvpView().showLoadingFileFromServerError();
                        },
                        () -> {

                        });
    }

    /**
     * Downloads a file from a given url
     * @param url {String} - the remote url from which the file will be downloaded
     * @param fileName {String} - the name of the downloaded file
     */
    public void downloadFile(String url, String fileName) {
        checkViewAttached();

        if (fileDownloadSubscription != null && !fileDownloadSubscription.isUnsubscribed())
            fileDownloadSubscription.unsubscribe();

        fileDownloadSubscription = mDataManager.downloadFile(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (responseBody) -> {
                            getMvpView().saveFile(responseBody, fileName);
                        },
                        error -> {
                            Timber.e(error, "There was an error loading file from Server.");
                            getMvpView().showLoadingFileFromServerError();
                        },
                        () -> {

                        });
    }

    public void openFileChooser() {
        // todo: show file chooser
        // todo: generate uploadLink for chosen file
        // todo: push file to server
    }

    /**
     * Opens a directory by fetching files for new storageContext
     * @param dirName {String} - the directory's name for which the files will be fetched
     */
    public void goIntoDirectory(String dirName) {
        mDataManager.setCurrentStorageContext(dirName);
        getMvpView().reloadFiles();
    }

    public void checkSignedIn(Context context) {
        super.isAlreadySignedIn(mDataManager, context);
    }
}

