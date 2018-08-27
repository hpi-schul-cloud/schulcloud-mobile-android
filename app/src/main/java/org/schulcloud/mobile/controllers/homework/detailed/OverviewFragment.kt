package org.schulcloud.mobile.controllers.homework.detailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.databinding.FragmentHomeworkOverviewBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository


class OverviewFragment : HomeworkTabFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkOverviewBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomework(viewModel.id)
    }
}
