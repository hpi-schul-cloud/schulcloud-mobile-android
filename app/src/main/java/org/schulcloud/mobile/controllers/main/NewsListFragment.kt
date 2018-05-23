package org.schulcloud.mobile.controllers.main


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.news.News

class NewsListFragment : BaseFragment() {

    companion object {
        val TAG: String = NewsListFragment::class.java.simpleName
    }

    private var newsListAdapter: NewsListAdapter? = null
    //dummy data
    private var newsData : List<News> = listOf (
            News ("title 1", "01.01.2018", "content 1"),
            News ("title 2", "02.02.2018", "content 2 content\n content\n content\n content content"),
            News ("title 3", "03.03.2018", "content 3"),
            News ("title 4", "04.04.2018", "content 4"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set viewModel here
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.title_list_news)
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = activity!!.findViewById<RecyclerView>(R.id.recycler_view_news)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        newsListAdapter = NewsListAdapter()

        //modify to handle ViewModels
        newsListAdapter!!.update(newsData!!)

        recyclerView.adapter = newsListAdapter
    }


}
