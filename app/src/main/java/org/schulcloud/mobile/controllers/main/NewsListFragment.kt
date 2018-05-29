package org.schulcloud.mobile.controllers.main


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmResults
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.course.CourseDao
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.viewmodels.NewsListViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class NewsListFragment : BaseFragment() {

    companion object {
        val TAG: String = NewsListFragment::class.java.simpleName
    }

    private var newsListAdapter: NewsListAdapter? = null
    private var newsListViewModel: NewsListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsListViewModel = ViewModelProviders.of(this).get(NewsListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.title_list_news)
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsListViewModel?.getNews()?.observe(this, Observer<RealmResults<News>> {
            news -> newsListAdapter!!.update(news!!)
        })

        val recyclerView = activity!!.findViewById<RecyclerView>(R.id.recycler_view_news)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        newsListAdapter = NewsListAdapter()
        recyclerView.adapter = newsListAdapter
    }


}
