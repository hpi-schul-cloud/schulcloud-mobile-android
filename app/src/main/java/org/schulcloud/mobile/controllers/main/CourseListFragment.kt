package org.schulcloud.mobile.controllers.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmResults
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.controllers.course.CourseActivity
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.viewmodels.CourseListViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class CourseListFragment : BaseFragment() {

    companion object {
        val TAG: String = CourseListFragment::class.java.simpleName
    }

    private var courseListViewModel: CourseListViewModel? = null
    private var courseListAdapter: CourseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseListViewModel = ViewModelProviders.of(this).get(CourseListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.title = getString(R.string.title_list_courses)
        return inflater.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        courseListViewModel?.getCourses()?.observe(this, Observer<RealmResults<Course>> { courses ->
            courseListAdapter!!.update(courses!!)
        })

        //
        val recyclerView = activity!!.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_spacing))
        courseListAdapter = CourseListAdapter { startActivity(CourseActivity.newIntent(context!!, it)) }
        recyclerView.adapter = courseListAdapter
    }
}
