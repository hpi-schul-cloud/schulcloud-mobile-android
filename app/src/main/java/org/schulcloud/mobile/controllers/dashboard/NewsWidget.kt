package org.schulcloud.mobile.controllers.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.widget_news.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.news.NewsAdapter
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.utils.limit
import org.schulcloud.mobile.viewmodels.NewsListViewModel
import org.schulcloud.mobile.views.DividerItemDecoration
import org.schulcloud.mobile.views.NoScrollLinearLayoutManager

class NewsWidget : Widget() {
    companion object {
        val TAG: String = NewsWidget::class.java.simpleName
        const val LIMIT = 5
    }

    private lateinit var viewModel: NewsListViewModel
    private val newsAdapter: NewsAdapter by lazy {
        NewsAdapter()
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
            newsAdapter.update(news?.limit(LIMIT) ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = newsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.MIDDLE))
        }
        newsAdapter.emptyIndicator = empty

        more.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_dashboardFragment_to_newsListFragment))
    }

    override suspend fun refresh() {
        NewsRepository.syncNews()
    }
}
