package org.schulcloud.mobile.controllers.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_course_list.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.viewmodels.CourseListViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class CourseListFragment : MainFragment() {
    companion object {
        val TAG: String = CourseListFragment::class.java.simpleName
    }

    override val config: MainFragmentConfig = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            fabIconRes = R.drawable.ic_newspaper_white_24dp
    )
    override var url: String? = "/courses"


    private lateinit var viewModel: CourseListViewModel
    private val coursesAdapter: CourseAdapter by lazy {
        CourseAdapter {
            val action = CourseListFragmentDirections
                    .actionFragmentCourseListToFragmentCourse(it)
            findNavController(this).navigate(action)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CourseListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coursesAdapter.emptyIndicator = empty
        viewModel.courses.observe(this, Observer { courses ->
            coursesAdapter.update(courses!!)
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = coursesAdapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_spacing))
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(R.string.course_title)
    }

    override suspend fun refresh() {
        CourseRepository.syncCourses()
    }
}
