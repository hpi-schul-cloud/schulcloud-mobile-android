@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import org.schulcloud.mobile.R
import org.schulcloud.mobile.config.Config
import org.schulcloud.mobile.controllers.base.ContextAware
import org.schulcloud.mobile.models.file.FileRepository
import java.io.IOException
import java.io.InputStream
import java.io.File as JavaFile


private const val TAG = "FileUtils"

fun JavaFile.create(): Boolean {
    return try {
        if (!parentFile.exists() && !parentFile.mkdirs())
            return false
        else if (exists() && !delete())
            return false
        else if (!createNewFile())
            return false
        else true
    } catch (e: IOException) {
        Log.e(TAG, e.toString())
        false
    }
}

fun JavaFile.saveDelete(): Boolean {
    return try {
        !exists() || delete()
        true
    } catch (e: IOException) {
        false
    }
}


suspend fun ContextAware.createFilePickerIntent(): Intent? {
    if (!requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        currentContext.showGenericError(R.string.file_pick_error_readPermissionDenied)
        return null
    }

    return Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }
}

fun Context.createTakePhotoIntent(): TakePhotoInfo? {
    // Create temp file used by camera app to store the photo
    val tempPicture = java.io.File(filesDir, combinePath("temp", "photo.jpg"))
    if (!tempPicture.create()) {
        showGenericError(R.string.file_pick_error_createTempFile)
        return null
    }

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val uri = FileProvider.getUriForFile(this, Config.FILE_PROVIDER, tempPicture)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

    // Test if apps can handle the intent, i.e. a camera app is installed
    if (intent.resolveActivity(packageManager) == null) {
        showGenericError(R.string.file_pick_error_noCamera)
        return null
    }

    // Grant temporary write permission for the destination file to all resolved activities
    val intentActivities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in intentActivities)
        grantUriPermission(resolveInfo.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

    return TakePhotoInfo(intent, tempPicture, uri)
}

data class TakePhotoInfo(
    val intent: Intent,
    val tempFile: JavaFile,
    val tempFileUri: Uri
)


fun Context.prepareFileRead(uri: Uri): FileReadInfo? {
    val (name, size) = uri.let {
        @Suppress("Recycle")
        contentResolver?.query(it, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        return@use cursor.getString(nameIndex) to cursor.getLong(sizeIndex)
    } ?: return null

    return FileReadInfo(name, size) { contentResolver.openInputStream(uri) }
}

suspend fun Context.uploadFile(uri: Uri?, path: String = FileRepository.pathPersonal(), name: String? = null) {
    val fileReadInfo = uri?.let { prepareFileRead(it) }
    if (fileReadInfo == null) {
        showGenericError(R.string.file_pick_error_read)
        return
    }

    withProgressDialog(R.string.file_fileUpload_progress) {
        val fileName = when {
            name == null -> fileReadInfo.name
            name.fileExtension.isEmpty() -> "$name.${fileReadInfo.name.fileExtension}"
            else -> name
        }
        val res = FileRepository.upload(path, fileName, fileReadInfo.size) {
            fileReadInfo.streamGenerator().also {
                if (it == null)
                    showGenericError(R.string.file_pick_error_read)
            }
        }
        if (!res) showGenericError(R.string.file_fileUpload_error_upload)
        else showGenericSuccess(R.string.file_fileUpload_success)

        FileRepository.syncDirectory(path)
    }
}

data class FileReadInfo(
    val name: String,
    val size: Long,
    val streamGenerator: () -> InputStream?
)
