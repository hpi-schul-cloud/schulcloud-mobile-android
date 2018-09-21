package org.schulcloud.mobile.controllers.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.controllers.base.BaseSheet
import org.schulcloud.mobile.databinding.SheetMainFileOpenBinding
import org.schulcloud.mobile.utils.createFilePickerIntent
import org.schulcloud.mobile.utils.showGenericError
import org.schulcloud.mobile.utils.uploadFile


class OpenFileSheet : BaseSheet() {
    companion object {
        private const val ARGUMENT_URI = "ARGUMENT_URI"

        fun forIncomingIntent(context: Context, intent: Intent): OpenFileSheet? {
            val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
            return when {
                intent.action != Intent.ACTION_SEND -> null
                uri == null -> {
                    context.showGenericError("Die Datei konnte nicht geöffnet werden")
                    null
                }
                else -> OpenFileSheet().apply {
                    arguments = bundleOf(ARGUMENT_URI to uri)
                }
            }
        }
    }

    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uri = arguments?.getParcelable(ARGUMENT_URI)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = SheetMainFileOpenBinding.inflate(layoutInflater).also {
            it.setLifecycleOwner(this)
            val context = context!!

            it.onUseForSubmission = {
                launch(UI) {
                    val res = startActivityForResult(createFilePickerIntent() ?: return@launch)
                    if (!res.success) return@launch

                    val file = context.uploadFile(uri)
                    //                    if (file != null)
                    //                        viewModel.submission.first()
                    //                                .observe(this, Observer {
                    //                                    it ?: return@Observer
                    //
                    //                                    launch {
                    //                                        viewModel.addFileToSubmission(it, file)
                    //                                    }
                    //                                })
                    dismiss()
                }
            }
            it.onUploadToFiles = {
                launch(UI) {
                    val res = startActivityForResult(createFilePickerIntent() ?: return@launch)
                    if (!res.success) return@launch

                    val file = context.uploadFile(res.data?.data)
                    dismiss()
                }
            }
        }
        return binding.root
    }
}
