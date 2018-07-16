package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.utils.HOST
import org.schulcloud.mobile.viewmodels.NewsListViewModel

class NewsListFragment : BaseFragment() {

    companion object {
        val TAG: String = NewsListFragment::class.java.simpleName
    }

    override var url: String? = "$HOST/news"

    private val newsListAdapter: NewsListAdapter by lazy {
        NewsListAdapter().apply {
            emptyIndicator = empty
        }
    }
    private lateinit var viewModel: NewsListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(NewsListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.news_title)
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsListAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.getNews().observe(this, Observer { news ->
            newsListAdapter.update(news!!)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_news_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        NewsRepository.syncNews()
    }
}
