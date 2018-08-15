package org.schulcloud.mobile.controllers.topic

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_topic.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentTopicBinding
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.dpToPx
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import org.schulcloud.mobile.viewmodels.TopicViewModel
import org.schulcloud.mobile.views.ItemOffsetDecoration

class TopicFragment : MainFragment() {
    companion object {
        val TAG: String = TopicFragment::class.java.simpleName
    }

    override var url: String? = null
        get() = viewModel.topic.value?.url

    override fun provideConfig() = MainFragmentConfig(
            title = viewModel.topic.value?.name ?: getString(R.string.general_error_notFound),
            menuBottomRes = R.menu.fragment_topic_bottom
    )

    private lateinit var viewModel: TopicViewModel
    private val contentsAdapter: ContentListAdapter by lazy {
        org.schulcloud.mobile.controllers.topic.ContentListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args = TopicFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.id))
                .get(TopicViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentTopicBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.topic.observe(this, Observer {
            notifyConfigChanged()
            contentsAdapter.update(it)
        })

        // calculate amount of columns
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val spans = Math.max(1, metrics.widthPixels / 440.dpToPx())

        contentsAdapter.emptyIndicator = empty
        recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(spans, StaggeredGridLayoutManager.VERTICAL)
            adapter = contentsAdapter
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.content_spacing))
        }
    }

    override suspend fun refresh() {
        viewModel.topic.value?.also {
            TopicRepository.syncTopic(it.id)
        }
    }
}
