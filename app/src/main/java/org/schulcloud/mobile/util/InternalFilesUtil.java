package org.schulcloud.mobile.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class InternalFilesUtil {

    private Activity mActivity;

    public InternalFilesUtil(@NonNull Activity activity) {
        mActivity = activity;
    }

    /**
     * saves a file to local storage
     *
     * @param body     {ResponseBody} - the given file which will be saved
     * @param fileName {String} - the name of the file
     * @return whether it was successful or not
     */
    public boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        String downloadDirPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        try {
            File futureFile = new File(downloadDirPath + File.separator + fileName);
            if (!futureFile.exists()) futureFile.createNewFile();

            OutputStream outputStream = new FileOutputStream(futureFile);
            outputStream.write(body.bytes());
            outputStream.close();

            DialogFactory.createSuperToast(mActivity,
                    mActivity.getString(R.string.files_save_success, fileName),
                    PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                    .show();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            DialogFactory.createSuperToast(mActivity,
                    mActivity.getResources().getString(R.string.files_save_error),
                    PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_RED))
                    .show();

            return false;
        }
    }


    /**
     * Opens a file chooser with a local installed file-chooser
     *
     * @param resultActionCode {Integer} - the id for the result action in the @context
     */
    public void openFileChooser(Integer resultActionCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        mActivity.startActivityForResult(intent, resultActionCode);
    }

    /**
     * Gets a file from a given uri to the devices content db
     *
     * @param contentPath {Uri} - the contentUri which references the file in the content db
     * @return a file representing the file on the device
     */
    public File getFileFromContentPath(Uri contentPath) {
        return FileUtils.getFile(mActivity, contentPath);
    }

}
