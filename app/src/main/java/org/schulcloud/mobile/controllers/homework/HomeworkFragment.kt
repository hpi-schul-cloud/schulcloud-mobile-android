package org.schulcloud.mobile.controllers.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentHomeworkBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class HomeworkFragment : MainFragment() {
    companion object {
        val TAG: String = HomeworkFragment::class.java.simpleName
    }

    private lateinit var viewModel: HomeworkViewModel


    override var url: String? = null
        get() = "homework/${viewModel.homework.value?.id}"

    override fun provideConfig() = MainFragmentConfig(
            title = viewModel.homework.value?.title ?: getString(R.string.general_error_notFound)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = HomeworkFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(HomeworkViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homework.observe(this, Observer {
            notifyConfigChanged()
        })
    }

    override suspend fun refresh() {
        viewModel.homework.value?.also {
            HomeworkRepository.syncHomework(it.id)
        }
    }
}
