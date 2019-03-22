package org.schulcloud.mobile.controllers.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.widget_news.*
import org.joda.time.Days
import org.joda.time.LocalDateTime
import org.schulcloud.mobile.R
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
            findNavController(this)
                    .navigate(R.id.action_dashboardFragment_to_homeworkListFragment)
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
                if (days == 0)
                    context!!.getString(R.string.dashboard_homework_dueToday)
                else
                    context!!.resources.getQuantityString(R.plurals.dashboard_homework_due, days, days)
            }

            adapter.update(homework ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = this@HomeworkWidget.adapter
        }

        more.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_dashboardFragment_to_homeworkListFragment))
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomeworkList()
    }
}
