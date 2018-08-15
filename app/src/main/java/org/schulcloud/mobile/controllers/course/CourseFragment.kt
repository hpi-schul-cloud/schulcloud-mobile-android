package org.schulcloud.mobile.controllers.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_course.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.controllers.topic.TopicActivity
import org.schulcloud.mobile.databinding.FragmentCourseBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.viewmodels.CourseViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class CourseFragment : MainFragment() {
    companion object {
        val TAG: String = CourseFragment::class.java.simpleName
    }

    private lateinit var viewModel: CourseViewModel
    private val topicsAdapter: TopicListAdapter by lazy {
        org.schulcloud.mobile.controllers.course.TopicListAdapter { id ->
            startActivity(TopicActivity.newIntent(context!!, id))
        }
    }


    override var url: String? = null
        get() = viewModel.course.value?.url

    override fun provideConfig() = MainFragmentConfig(
            title = viewModel.course.value?.name ?: getString(R.string.general_error_notFound)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = CourseFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(CourseViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCourseBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.course.observe(this, Observer {
            notifyConfigChanged()
        })

        topicsAdapter.emptyIndicator = empty
        viewModel.topics.observe(this, Observer {
            topicsAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override suspend fun refresh() {
        viewModel.course.value?.also {
            CourseRepository.syncCourse(it.id)
            TopicRepository.syncTopics(it.id)
        }
    }
}
