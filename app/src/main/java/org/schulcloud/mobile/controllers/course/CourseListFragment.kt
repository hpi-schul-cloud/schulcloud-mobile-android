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

    override var url: String? = "/courses"


    private lateinit var viewModel: CourseListViewModel
    private val courseAdapter: CourseAdapter by lazy {
        CourseAdapter {
            findNavController(this).navigate(
                    R.id.action_global_fragment_course,
                    CourseFragmentArgs.Builder(it).build().toBundle())
        }
    }

    override fun provideConfig() = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = getString(R.string.course_title)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CourseListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseAdapter.emptyIndicator = empty
        viewModel.courses.observe(this, Observer { courses ->
            courseAdapter.update(courses!!)
        })

        recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = courseAdapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_spacing))
        }
    }


    override suspend fun refresh() {
        CourseRepository.syncCourses()
    }
}
