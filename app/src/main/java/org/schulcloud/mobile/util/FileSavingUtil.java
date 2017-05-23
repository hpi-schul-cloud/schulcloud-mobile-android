package org.schulcloud.mobile.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.schulcloud.mobile.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class FileSavingUtil {

    private final static Integer FILE_WRITER_PERMISSION_CALLBACK_ID = 43;

    /**
     * saves a file to local storage
     *
     * @param body     {ResponseBody} - the given file which will be saved
     * @param fileName {String} - the name of the file
     * @param context  {Context} - a application context
     * @return whether it was successful or not
     */
    public static boolean writeResponseBodyToDisk(ResponseBody body, String fileName, Context context) {
        String downloadDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        PermissionsUtil.checkPermissions(FILE_WRITER_PERMISSION_CALLBACK_ID, (Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        OutputStream outputStream = null;
        try {
            File futureFile = new File(downloadDirPath + File.separator + fileName);


            if (!futureFile.exists()) futureFile.createNewFile();

            outputStream = new FileOutputStream(futureFile);
            outputStream.write(body.bytes());
            outputStream.close();

            String message = context.getString(R.string.file_save_success, fileName);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.file_save_failed, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
