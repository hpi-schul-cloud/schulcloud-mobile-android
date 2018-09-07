package org.schulcloud.mobile.controllers.file

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_create_directory.*
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.jobs.CreateDirectoryJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.combinePath

class CreateDirectoryFragment(): DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var inflater = activity.layoutInflater
        var path = savedInstanceState?.get("path").toString()

        builder.setView(inflater.inflate(R.layout.fragment_create_directory,null))
        builder.setNegativeButton(resources.getString(android.R.string.cancel),object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                getDialog().cancel()
            }
        })
        builder.setPositiveButton(resources.getString(android.R.string.ok),object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                var dirName = files_create_directory_edit.text.toString()
                async {
                    CreateDirectoryJob(combinePath(path,dirName),object : RequestJobCallback() {
                        override fun onError(code: ErrorCode) {}
                        override fun onSuccess() {}
                    })
                    getDialog().cancel()
                }
            }
        })

        return builder.create()
    }
}
