package org.schulcloud.mobile.controllers.homework

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_homework_overview.*
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.main.Refreshable
import org.schulcloud.mobile.controllers.main.RefreshableImpl
import org.schulcloud.mobile.databinding.FragmentHomeworkOverviewBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.HomeworkViewModel


@SuppressLint("ValidFragment")
class OverviewFragment(private val refreshableImpl: RefreshableImpl = RefreshableImpl()) : BaseFragment(),
        Refreshable by refreshableImpl {

    companion object {
        val TAG: String = OverviewFragment::class.java.simpleName
    }

    private lateinit var viewModel: HomeworkViewModel

    init {
        refreshableImpl.refresh = { HomeworkRepository.syncHomework(viewModel.id) }
    }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshableImpl.swipeRefreshLayout = swipeRefresh
    }
}
