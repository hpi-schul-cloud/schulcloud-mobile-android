package org.schulcloud.mobile.controllers.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository


class SubmissionFragment : HomeworkTabFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomework(viewModel.id)
        viewModel.selectedSubmission.value?.id?.also { SubmissionRepository.syncSubmission(it) }
                ?: SubmissionRepository.syncSubmissionsForHomework(viewModel.id)
    }
}
