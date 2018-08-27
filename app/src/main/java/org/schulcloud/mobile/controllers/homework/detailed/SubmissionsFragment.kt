package org.schulcloud.mobile.controllers.homework.detailed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_homework_submissions.*
import org.schulcloud.mobile.databinding.FragmentHomeworkSubmissionsBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.utils.combineLatest
import org.schulcloud.mobile.views.DividerItemDecoration


class SubmissionsFragment : HomeworkTabFragment() {
    private val submissionsAdapter by lazy {
        SubmissionsAdapter {
            viewModel.selectionByUser = true
            viewModel.selectedStudent.value = it
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentHomeworkSubmissionsBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submissionsAdapter.emptyIndicator = empty
        viewModel.homework
                .combineLatest(viewModel.submissions, viewModel.selectedStudent)
                .observe(this, Observer { (homework, submissions, selectedStudent) ->
                    if (homework == null)
                        return@Observer

                    submissionsAdapter.update(homework, submissions, selectedStudent?.id)
                })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = submissionsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
    }

    override suspend fun refresh() {
        HomeworkRepository.syncHomework(viewModel.id)
        SubmissionRepository.syncSubmissionsForHomework(viewModel.id)
        viewModel.homework.value?.course?.id?.also { CourseRepository.syncCourse(it) }
    }
}
