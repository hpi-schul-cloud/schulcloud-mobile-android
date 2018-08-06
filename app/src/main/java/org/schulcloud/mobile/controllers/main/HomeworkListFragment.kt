package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_homework_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.homework.HomeworkActivity
import org.schulcloud.mobile.viewmodels.HomeworkListViewModel

class HomeworkListFragment : BaseFragment() {

    companion object {
        val TAG: String = HomeworkListFragment::class.java.simpleName
    }

//    override var url: String? = "${HOST}/homework"

    private lateinit var viewModel: HomeworkListViewModel
    private val homeworkListAdapter: HomeworkListAdapter by lazy {
        HomeworkListAdapter{
            startActivity(HomeworkActivity.newIntent(context!!,it))
        }.apply {
            emptyIndicator = empty
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(HomeworkListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.homework_title)
        return inflater.inflate(R.layout.fragment_homework_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        swipeRefreshLayout = swipeRefresh

        viewModel.homework.observe(this, Observer {
            homeworkListAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeworkListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

//    override suspend fun refresh() {
//        HomeworkRepository.syncHomeworkList()
//    }
}
