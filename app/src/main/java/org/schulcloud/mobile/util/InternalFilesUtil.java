package org.schulcloud.mobile.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import org.schulcloud.mobile.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class InternalFilesUtil {

    private Context context;

    public InternalFilesUtil(Context context) {
        this.context = context;
    }

    /**
     * saves a file to local storage
     *
     * @param body     {ResponseBody} - the given file which will be saved
     * @param fileName {String} - the name of the file
     * @return whether it was successful or not
     */
    public boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        String downloadDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
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


    /**
     * Opens a file chooser with a local installed file-chooser
     * @param resultActionCode {Integer} - the id for the result action in the @context
     */
    public void openFileChooser(Integer resultActionCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        ((Activity) context).startActivityForResult(intent, resultActionCode);
    }

    /**
     * Gets a file from a given uri to the devices content db
     * @param contentPath {Uri} - the contentUri which references the file in the content db
     */
    public void getFileFromContentPath(Uri contentPath) {
        File file = FileUtils.getFile(context, contentPath);
        System.out.println(file);
        /**System.out.println(contentPath);
        String pathName = "tmp/" + contentPath.getPath();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(contentPath);
            File file = new File(pathName);
                    OutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }**/
    }

}
