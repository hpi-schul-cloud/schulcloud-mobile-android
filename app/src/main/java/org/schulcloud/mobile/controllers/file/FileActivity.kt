package org.schulcloud.mobile.controllers.file

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.controllers.base.OnItemSelectedCallback
import org.schulcloud.mobile.databinding.ActivityFileBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.utils.combinePath
import org.schulcloud.mobile.utils.getPathParts
import org.schulcloud.mobile.utils.map
import org.schulcloud.mobile.utils.visibilityBool
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory

class FileActivity : BaseActivity() {
    companion object {
        val TAG: String = FileActivity::class.java.simpleName
        const val EXTRA_PATH = "org.schulcloud.extras.EXTRA_PATH"

        fun newIntent(context: Context, path: String): Intent {
            return Intent(context, FileActivity::class.java)
                    .apply { putExtra(EXTRA_PATH, path) }
        }
    }

    private lateinit var binding: ActivityFileBinding
    private lateinit var viewModel: FileViewModel
    private val directoryAdapter: DirectoryAdapter by lazy {
        DirectoryAdapter(OnItemSelectedCallback { name ->
            startActivity(newIntent(this, combinePath(viewModel.path, name)))
        })
    }
    private val fileAdapter: FileAdapter by lazy {
        FileAdapter(OnItemSelectedCallback { name ->

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFileBinding.inflate(layoutInflater)
        binding.setLifecycleOwner(this)
        setContentView(binding.root)

        directories_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FileActivity)
            adapter = directoryAdapter
            addItemDecoration(DividerItemDecoration(this@FileActivity, DividerItemDecoration.VERTICAL))
        }
        files_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FileActivity)
            adapter = fileAdapter
            addItemDecoration(DividerItemDecoration(this@FileActivity, DividerItemDecoration.VERTICAL))
        }

        init(intent)

        setupActionBar()
        swipeRefreshLayout = swipeRefresh
        performRefresh()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init(intent!!)
        performRefresh()
    }

    private fun init(intent: Intent) {
        val path = intent.getStringExtra(EXTRA_PATH)
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(path))
                .get(path, FileViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.directories.observe(this, Observer {
            directoryAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })
        viewModel.files.observe(this, Observer {
            fileAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })

        // Title
        val parts = path.getPathParts()
        when {
            parts.size > 2 ->
                title = parts.last()
            path.startsWith(FileRepository.CONTEXT_MY_API) ->
                title = getString(R.string.file_directory_my)
            path.startsWith(FileRepository.CONTEXT_COURSES) -> {
                launch { CourseRepository.syncCourse(parts[1]) }
                CourseRepository.course(viewModel.realm, parts[1]).map {
                    it?.name ?: getString(R.string.file_directory_course_unknown)
                }.observe(this, Observer { title = it })
            }
            else -> throw IllegalArgumentException("Path $path is not supported")
        }
    }

    private fun updateEmptyMessage() {
        empty.visibilityBool = (viewModel.directories.value?.isEmpty() ?: false)
                && (viewModel.files.value?.isEmpty() ?: false)
    }

    override suspend fun refresh() {
        FileRepository.syncDirectory(viewModel.path)
    }
}
