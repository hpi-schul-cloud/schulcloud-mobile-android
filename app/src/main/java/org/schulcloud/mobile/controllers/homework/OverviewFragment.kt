package org.schulcloud.mobile.controllers.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.Refreshable
import org.schulcloud.mobile.databinding.FragmentHomeworkOverviewBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.HomeworkViewModel

class OverviewFragment : BaseFragment(), Refreshable {
    companion object {
        val TAG: String = OverviewFragment::class.java.simpleName
    }

    private lateinit var viewModel: HomeworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = (parentFragment as HomeworkFragment).viewModel
        super.onCreate(savedInstanceState)
    }

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
