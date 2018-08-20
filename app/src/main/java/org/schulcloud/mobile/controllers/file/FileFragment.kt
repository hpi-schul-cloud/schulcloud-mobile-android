package org.schulcloud.mobile.controllers.file

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentFileBinding
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await


class FileFragment : MainFragment() {
    companion object {
        val TAG: String = FileFragment::class.java.simpleName
    }

    private val args: FileFragmentArgs by lazy {
        FileFragmentArgs.fromBundle(arguments)
    }
    private lateinit var viewModel: FileViewModel
    private val directoryAdapter: DirectoryAdapter by lazy {
        DirectoryAdapter {
            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(combinePath(viewModel.path, it)).build().toBundle())
        }
    }
    private val fileAdapter: FileAdapter by lazy {
        FileAdapter({ loadFile(it, false) },
                { loadFile(it, true) })
    }

    override fun provideConfig() = (getCourseFromFolder()?.let {
        CourseRepository.course(viewModel.realm, it)
    } ?: null.asLiveData<Course>())
            .map { course ->
                breadcrumbs.setPath(args.path, course)

                MainFragmentConfig(
                        title = null,
                        toolbarColor = course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = R.menu.fragment_file_bottom,
                        menuBottomHiddenIds = listOf(
                                if (course == null) R.id.file_action_gotoCourse else 0
                        )
                )
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.path))
                .get(FileViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentFileBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun updateEmptyMessage() {
            empty.visibilityBool = (viewModel.directories.value?.isEmpty() ?: false)
                    && (viewModel.files.value?.isEmpty() ?: false)
        }

        viewModel.directories.observe(this, Observer {
            directoryAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })
        directories_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = directoryAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.files.observe(this, Observer {
            fileAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })
        files_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fileAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onResume() {
        super.onResume()

        mainActivity.setToolbarWrapper(toolbarWrapper)

        breadcrumbs.setPath(args.path)
        breadcrumbs.onPathSelected = { path ->
            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(path).build().toBundle())
        }
        mainViewModel.toolbarColors.observe(this, Observer {
            breadcrumbs.setTextColor(it.textColor)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.file_action_gotoCourse ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(getCourseFromFolder()!!).build().toBundle())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        FileRepository.syncDirectory(viewModel.path)
        getCourseFromFolder()?.also {
            CourseRepository.syncCourse(it)
        }
    }


    private fun getCourseFromFolder(): String? {
        val parts = args.path.getPathParts()
        if (!args.path.startsWith(FileRepository.CONTEXT_COURSES))
            return null
        return parts[1]
    }

    private fun loadFile(file: File, download: Boolean) = launch(UI) {
        try {
            val response = ApiService.getInstance().generateSignedUrl(
                    SignedUrlRequest().apply {
                        action = SignedUrlRequest.ACTION_GET
                        path = file.key
                        fileType = file.type
                    }).await()

            if (download) {
                if (!requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    this@FileFragment.context?.showGenericError(R.string.file_fileDownload_error_savePermissionDenied)
                    return@launch
                }

                this@FileFragment.context?.withProgressDialog(R.string.file_fileDownload_progress) {
                    val result = ApiService.getInstance().downloadFile(response.url!!).await()
                    if (!result.writeToDisk(file.name.orEmpty())) {
                        this@FileFragment.context?.showGenericError(R.string.file_fileDownload_error_save)
                        return@withProgressDialog
                    }
                    this@FileFragment.context?.showGenericSuccess(getString(R.string.file_fileDownload_success, file.name))
                }

            } else {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(response.url), response.header?.contentType)
                }
                if (intent.resolveActivity(activity?.packageManager) != null)
                    startActivity(intent)
                else
                    this@FileFragment.context?.showGenericError(getString(R.string.file_fileOpen_error_cantResolve, file.name?.fileExtension))
            }
        } catch (e: HttpException) {
            when (e.code()) {
                404 -> this@FileFragment.context?.showGenericError(R.string.file_fileOpen_error_404)
            }
        }
    }
}