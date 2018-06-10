package org.schulcloud.mobile.controllers.course

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.databinding.ActivityCourseBinding
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.viewmodels.CourseViewModel
import org.schulcloud.mobile.viewmodels.CourseViewModelFactory

/**
 * Date: 6/9/2018
 */
class CourseActivity : BaseActivity() {

    companion object {
        val TAG: String = CourseActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"

        fun newIntent(context: Context, id: String): Intent {
            val intent = Intent(context, CourseActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            return intent
        }
    }

    private var viewModel: CourseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, CourseViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(CourseViewModel::class.java)
        val binding = ActivityCourseBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()

        viewModel?.course?.observe(this, Observer<Course> { course -> Log.d(TAG, course?.id) })
    }
}
