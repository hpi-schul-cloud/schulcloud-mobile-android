package org.schulcloud.mobile.controllers.homework.attachment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.databinding.SheetHomeworkAttachmentAddBinding
import org.schulcloud.mobile.utils.createFilePickerIntent
import org.schulcloud.mobile.utils.uploadFile

class AddAttachmentSheet : BottomSheetDialogFragment() {
    companion object {
        private const val REQUEST_FILE_TO_UPLOAD = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = SheetHomeworkAttachmentAddBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)

            it.onAttachFile = {
                startActivityForResult(createFilePickerIntent(), REQUEST_FILE_TO_UPLOAD)
            }
            it.onAttachTakePhoto = {

            }
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FILE_TO_UPLOAD) {
            // Probably cancelled by user -> don't show error message
            if (resultCode != Activity.RESULT_OK) return

            launch(UI) {
                getContext()!!.uploadFile(data?.data)
                dismiss()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
