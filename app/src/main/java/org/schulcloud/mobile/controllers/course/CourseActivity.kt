package org.schulcloud.mobile.controllers.course

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_course.*
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.databinding.ActivityCourseBinding
import org.schulcloud.mobile.models.topic.Topic
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
    private var topicsAdapter: TopicListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, CourseViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(CourseViewModel::class.java)
        val binding = ActivityCourseBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        topicsAdapter = TopicListAdapter(object : TopicListAdapter.OnTopicSelectedCallback {
            override fun onTopicSelected(id: String) {
                Toast.makeText(this@CourseActivity, "Selected $id", Toast.LENGTH_SHORT).show()
            }
        })
        recycler_view.adapter = topicsAdapter

        viewModel?.topics?.observe(this, Observer<RealmResults<Topic>> { topics ->
            topicsAdapter!!.update(topics!!)
        })
    }
}
