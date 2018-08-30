package org.schulcloud.mobile.controllers.homework.submission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.main.TabFragment
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionOverviewBinding
import org.schulcloud.mobile.viewmodels.SubmissionViewModel


class OverviewFragment : TabFragment<SubmissionFragment, SubmissionViewModel>() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionOverviewBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override suspend fun refresh() {
        (parentFragment as? SubmissionFragment)?.refreshWithChild(true)
    }
}
