package org.schulcloud.mobile.controllers.homework.submission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.homework.attachment.AddAttachmentSheet
import org.schulcloud.mobile.controllers.main.InnerMainFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionOverviewBinding
import org.schulcloud.mobile.viewmodels.SubmissionViewModel


class OverviewFragment : InnerMainFragment<OverviewFragment, SubmissionFragment, SubmissionViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionOverviewBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.submission_action_addAttachment -> {
                val sheet = AddAttachmentSheet()
                sheet.show(fragmentManager, sheet.tag)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {}
}
