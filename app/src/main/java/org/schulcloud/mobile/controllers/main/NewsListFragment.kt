package org.schulcloud.mobile.controllers.main


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment

class NewsListFragment : BaseFragment() {

    companion object {
        val TAG: String = NewsListFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.title_list_news)
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }


}
