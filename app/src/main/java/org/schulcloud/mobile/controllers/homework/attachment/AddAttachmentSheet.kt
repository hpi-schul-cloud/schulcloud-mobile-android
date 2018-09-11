package org.schulcloud.mobile.controllers.homework.attachment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.controllers.base.BaseSheet
import org.schulcloud.mobile.databinding.SheetHomeworkAttachmentAddBinding
import org.schulcloud.mobile.utils.createFilePickerIntent
import org.schulcloud.mobile.utils.uploadFile


class AddAttachmentSheet : BaseSheet() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = SheetHomeworkAttachmentAddBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)

            it.onAttachFile = {
                launch(UI) {
                    val res = startActivityForResult(createFilePickerIntent()) ?: return@launch
                    getContext()!!.uploadFile(res.data)
                    dismiss()
                }
            }
            it.onAttachTakePhoto = {

            }
        }
        return binding.root
    }
}
