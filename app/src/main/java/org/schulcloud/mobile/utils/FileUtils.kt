@file:Suppress("TooManyFunctions")

package org.schulcloud.mobile.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import org.schulcloud.mobile.R
import org.schulcloud.mobile.models.file.FileRepository
import java.io.InputStream


fun createFilePickerIntent(): Intent {
    return Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "*/*"
        addCategory(Intent.CATEGORY_OPENABLE)
    }
}

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

suspend fun Context.uploadFile(uri: Uri?, path: String = FileRepository.pathPersonal()) {
    val fileReadInfo = uri?.let { prepareFileRead(it) }
    if (fileReadInfo == null) {
        showGenericError(R.string.file_fileUpload_error_read)
        return
    }

    withProgressDialog(R.string.file_fileUpload_progress) {
        val res = FileRepository.upload(path, fileReadInfo.name, fileReadInfo.size) {
            fileReadInfo.streamGenerator().also {
                if (it == null)
                    showGenericError(R.string.file_fileUpload_error_read)
            }
        }
        if (!res) showGenericError(R.string.file_fileUpload_error_upload)

        FileRepository.syncDirectory(path)
    }
}

data class FileReadInfo(
    val name: String,
    val size: Long,
    val streamGenerator: () -> InputStream?
)
