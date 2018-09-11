package org.schulcloud.mobile.controllers.homework.attachment

import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_SHOW_DATE
import android.text.format.DateUtils.FORMAT_SHOW_TIME
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.controllers.base.BaseSheet
import org.schulcloud.mobile.databinding.SheetHomeworkAttachmentAddBinding
import org.schulcloud.mobile.utils.createFilePickerIntent
import org.schulcloud.mobile.utils.createTakePhotoIntent
import org.schulcloud.mobile.utils.saveDelete
import org.schulcloud.mobile.utils.uploadFile


class AddAttachmentSheet : BaseSheet() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = SheetHomeworkAttachmentAddBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)
            val context = context!!

            it.onAttachFile = {
                launch(UI) {
                    val res = startActivityForResult(createFilePickerIntent() ?: return@launch)
                    if (!res.success) return@launch

                    context.uploadFile(res.data?.data)
                    dismiss()
                }
            }
            it.onAttachTakePhoto = {
                launch(UI) {
                    val info = context.createTakePhotoIntent() ?: return@launch
                    val res = startActivityForResult(info.intent)
                    if (!res.success) return@launch

                    val name = DateUtils.formatDateTime(context, System.currentTimeMillis(),
                            FORMAT_SHOW_DATE or FORMAT_SHOW_TIME)
                    context.uploadFile(info.tempFileUri, name = name)
                    info.tempFile.saveDelete()
                    dismiss()
                }
            }
        }
        return binding.root
    }
}
