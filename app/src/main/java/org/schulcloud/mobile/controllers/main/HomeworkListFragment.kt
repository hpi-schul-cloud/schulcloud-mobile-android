package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmResults
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.viewmodels.HomeworkListViewModel
import kotlinx.android.synthetic.main.fragment_homework_list.*
import org.schulcloud.mobile.models.homework.Homework

class HomeworkListFragment : BaseFragment() {

    companion object {
        val TAG: String = HomeworkListFragment::class.java.simpleName
    }

    private var homeworkListAdapter: HomeworkListAdapter? = null
    private var homeworkListViewModel: HomeworkListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeworkListViewModel = ViewModelProviders.of(this).get(HomeworkListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.title_list_homework)
        return inflater.inflate(R.layout.fragment_homework_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeworkListAdapter = HomeworkListAdapter()
        recycler_view_homework.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = homeworkListAdapter
        }

        homeworkListViewModel!!.getHomework().observe(this, Observer<RealmResults<Homework>> { homework ->
            homeworkListAdapter!!.update(homework!!)
        })
    }
}

