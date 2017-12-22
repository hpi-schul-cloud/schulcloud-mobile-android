package org.schulcloud.mobile.ui.files;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

import okhttp3.ResponseBody;

public interface FileMvpView extends MvpView {

    void showBreadcrumbs(@NonNull String path, @Nullable Course course);

    void showFiles(@NonNull List<File> files);

    void showDirectories(@NonNull List<Directory> directories);

    void showError();

    void showLoadingFileFromServerError();

    void showFile(@NonNull String url, @NonNull String mimeType);

    void showUploadFileError();

    void reloadFiles();

    void saveFile(@NonNull ResponseBody body, @NonNull String fileName);

    void startFileChoosing();

    void startDownloading(@NonNull File file, boolean download);

    /* File deletion */
    void startFileDeleting(@NonNull String path, @NonNull String fileName);

    void showFileDeleteSuccess();

    void showFileDeleteError();

    /* Directory deletion */
    void startDirectoryDeleting(@NonNull String path, @NonNull String dirName);

    void showDirectoryDeleteSuccess();

    void showDirectoryDeleteError();

}
