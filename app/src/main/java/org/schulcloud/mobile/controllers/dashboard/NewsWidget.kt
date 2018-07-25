package org.schulcloud.mobile.controllers.dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.widget_news.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.NewsListAdapter
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.viewmodels.NewsListViewModel
import org.schulcloud.mobile.views.MiddleDividerItemDecoration
import org.schulcloud.mobile.views.NoScrollLinearLayoutManager

class NewsWidget : Widget() {
    companion object {
        val TAG: String = NewsWidget::class.java.simpleName
    }

    private lateinit var viewModel: NewsListViewModel
    private val newsAdapter: NewsListAdapter by lazy {
        NewsListAdapter().apply {
            emptyIndicator = empty
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.widget_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.news.observe(this, Observer { news ->
            newsAdapter.update(news ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = newsAdapter
            addItemDecoration(MiddleDividerItemDecoration(context))
        }
    }

    override suspend fun refresh() {
        NewsRepository.syncNews()
    }
}
