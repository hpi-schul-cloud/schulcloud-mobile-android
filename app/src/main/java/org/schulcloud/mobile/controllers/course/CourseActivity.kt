package org.schulcloud.mobile.controllers.course

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_course.*
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.controllers.topic.TopicActivity
import org.schulcloud.mobile.databinding.ActivityCourseBinding
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.viewmodels.CourseViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

/**
 * Date: 6/9/2018
 */
class CourseActivity : BaseActivity() {

    companion object {
        val TAG: String = CourseActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"

        fun newIntent(context: Context, id: String): Intent {
            return Intent(context, CourseActivity::class.java)
                    .apply { putExtra(EXTRA_ID, id) }
        }
    }

    private lateinit var viewModel: CourseViewModel
    private lateinit var topicsAdapter: TopicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(CourseViewModel::class.java)
        val binding = ActivityCourseBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        topicsAdapter = TopicListAdapter(OnItemSelectedCallback { id ->
            startActivity(TopicActivity.newIntent(this@CourseActivity, id))
        })
        recycler_view.adapter = topicsAdapter

        viewModel.topics.observe(this, Observer<RealmResults<Topic>> { topics ->
            topics?.also { topicsAdapter.update(it) }
        })
    }
}
