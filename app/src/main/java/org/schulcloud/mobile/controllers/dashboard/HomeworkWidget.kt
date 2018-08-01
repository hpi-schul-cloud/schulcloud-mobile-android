package org.schulcloud.mobile.controllers.dashboard

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.widget_news.*
import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.HomeworkListFragment
import org.schulcloud.mobile.databinding.WidgetHomeworkBinding
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
    private lateinit var binding: WidgetHomeworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeworkWidgetViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = WidgetHomeworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.homework.observe(this, Observer { homework ->
            binding.count = homework?.size ?: 0

            val nextTime = homework
                    ?.filter { it.dueDateTime != null }
                    ?.minBy { it.dueDateTime!! }
                    ?.dueDateTime

            binding.timeToNext = if (nextTime == null)
                ""
            else {
                val days = Days.daysBetween(LocalDateTime.now(), nextTime.toLocalDateTime()).days
                when (days) {
                    0 -> context!!.getString(R.string.dashboard_homework_due0)
                    1 -> context!!.getString(R.string.dashboard_homework_due1)
                    else -> context!!.getString(R.string.dashboard_homework_dueLater, days)
                }
            }

            adapter.update(homework ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = this@HomeworkWidget.adapter
        }

        more.setOnClickListener {
            showFragment(HomeworkListFragment(), HomeworkListFragment.TAG)
        }
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomeworkList()
    }
}
