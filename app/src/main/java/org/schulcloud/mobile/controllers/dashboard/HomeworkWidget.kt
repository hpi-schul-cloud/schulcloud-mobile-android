package org.schulcloud.mobile.controllers.dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.widget_news.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.HomeworkListFragment
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.HomeworkWidgetViewModel
import org.schulcloud.mobile.views.NoScrollLinearLayoutManager

class HomeworkWidget : Widget() {
    companion object {
        val TAG: String = HomeworkWidget::class.java.simpleName
    }

    private lateinit var viewModel: HomeworkWidgetViewModel
    private val adapter: HomeworkAdapter by lazy {
        HomeworkAdapter {
            showFragment(HomeworkListFragment(), HomeworkListFragment.TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeworkWidgetViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.widget_homework, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homework.observe(this, Observer { homework ->
            adapter.update(homework ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = this@HomeworkWidget.adapter
        }
        adapter.emptyIndicator = empty

        more.setOnClickListener {
            showFragment(HomeworkListFragment(), HomeworkListFragment.TAG)
        }
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomeworkList()
    }
}
