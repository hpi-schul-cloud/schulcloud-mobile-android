package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.viewmodels.HomeworkListViewModel
import kotlinx.android.synthetic.main.fragment_homework_list.*
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.utils.HOST

class HomeworkListFragment : BaseFragment() {

    companion object {
        val TAG: String = HomeworkListFragment::class.java.simpleName
    }

    override var url: String? = "${HOST}/homework"

    private lateinit var homeworkListViewModel: HomeworkListViewModel
    private val homeworkListAdapter: HomeworkListAdapter by lazy {
        HomeworkListAdapter(OnItemSelectedCallback {
           // startActivity(,it)
        }).apply {
            emptyIndicator = empty
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        homeworkListViewModel = ViewModelProviders.of(this).get(HomeworkListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.homework_title)
        return inflater.inflate(R.layout.fragment_homework_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = swipeRefresh

        homeworkListViewModel.homework.observe(this, Observer {
            homeworkListAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = homeworkListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_course_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomeworkList()
    }
}
