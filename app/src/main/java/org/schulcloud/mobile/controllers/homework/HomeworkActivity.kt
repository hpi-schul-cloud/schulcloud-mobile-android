package org.schulcloud.mobile.controllers.homework

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_homework.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.databinding.ActivityHomeworkBinding
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.viewmodels.HomeworkViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class HomeworkActivity : BaseActivity() {

    companion object {
        val TAG: String = HomeworkActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"

        fun newIntent(context: Context, id: String): Intent {
            return Intent(context, HomeworkActivity::class.java)
                    .apply { putExtra(EXTRA_ID, id) }
        }
    }

    override var url: String? = null
        get() = viewModel.homework.value?.id

    private lateinit var viewModel: HomeworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(intent.getStringExtra(EXTRA_ID)))
                .get(HomeworkViewModel::class.java)
        val binding = ActivityHomeworkBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        setupActionBar()
        swipeRefreshLayout = swipeRefresh
        performRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_base, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override suspend fun refresh() {
        viewModel.homework.value?.also {
            HomeworkRepository.syncHomework(it.id)
        }
    }
}
