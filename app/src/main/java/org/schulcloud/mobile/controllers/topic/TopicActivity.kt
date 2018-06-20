package org.schulcloud.mobile.controllers.topic

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.activity_topic.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.databinding.ActivityTopicBinding
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.dpToPx
import org.schulcloud.mobile.utils.syncOnRefresh
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.TopicViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration


/**
 * Date: 6/11/2018
 */
class TopicActivity : BaseActivity() {

    companion object {
        val TAG: String = TopicActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"

        fun newIntent(context: Context, id: String): Intent {
            return Intent(context, TopicActivity::class.java)
                    .apply { putExtra(EXTRA_ID, id) }
        }
    }

    private lateinit var viewModel: TopicViewModel
    private val contentsAdapter: ContentListAdapter by lazy { ContentListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, IdViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(TopicViewModel::class.java)
        val binding = ActivityTopicBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()

        // calculate amount of columns
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val spans = Math.max(1, metrics.widthPixels / 440.dpToPx())

        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(spans, StaggeredGridLayoutManager.VERTICAL)
            adapter = contentsAdapter
            addItemDecoration(ItemOffsetDecoration(this@TopicActivity, R.dimen.content_spacing))
        }

        viewModel.topic.observe(this, Observer { contentsAdapter.update(it) })

        swipeRefresh.syncOnRefresh {
            viewModel.topic.value?.also {
                TopicRepository.syncTopic(it.id)
            }
        }
    }
}
