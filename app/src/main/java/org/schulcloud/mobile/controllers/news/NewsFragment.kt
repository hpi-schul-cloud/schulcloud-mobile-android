package org.schulcloud.mobile.controllers.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentNewsBinding
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.NewsViewModel

class NewsFragment : MainFragment() {
    companion object {
        val TAG: String = NewsFragment::class.java.simpleName
    }

    private lateinit var viewModel: NewsViewModel


    override var url: String? = null
        get() = "news/${viewModel.news.value?.id}"

    override fun provideConfig() = MainFragmentConfig(
            title = viewModel.news.value?.title ?: getString(R.string.general_error_notFound)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = NewsFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(NewsViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentNewsBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.news.observe(this, Observer {
            notifyConfigChanged()
        })
    }

    override suspend fun refresh() {
        viewModel.news.value?.also {
            NewsRepository.syncNews(it.id)
        }
    }
}