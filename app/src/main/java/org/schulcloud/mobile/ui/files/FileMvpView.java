package org.schulcloud.mobile.ui.files;

import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.ui.base.MvpView;

import java.util.List;

import okhttp3.ResponseBody;


public interface FileMvpView extends MvpView {
    void showFiles(List<File> files);

    void showDirectories(List<Directory> directories);

    void showError();

    void showLoadingFileFromServerError();

    void showFile(String url, String mimeType);

    void reloadFiles();

    void saveFile(ResponseBody body, String fileName);
}
