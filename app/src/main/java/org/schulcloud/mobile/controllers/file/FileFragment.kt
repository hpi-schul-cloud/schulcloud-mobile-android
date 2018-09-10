package org.schulcloud.mobile.controllers.file

import android.Manifest
import android.app.Activity
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
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.user.Permission
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.models.user.hasPermission
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await
import java.io.File as JavaFile


class FileFragment : MainFragment<FileFragment, FileViewModel>() {
    companion object {
        private val TAG: String = FileFragment::class.java.simpleName

        private const val REQUEST_FILE_TO_UPLOAD = 1
    }

    private val args: FileFragmentArgs by lazy {
        FileFragmentArgs.fromBundle(arguments)
    }

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


    override var url: String? = null
        get() {
            val parts = args.path.getPathParts()
            val path = if (parts.size <= 2) ""
            else "?dir=${parts.takeLast(parts.size - 2).combinePath().ensureSlashes()}"

            return when (parts.first()) {
                FileRepository.CONTEXT_MY_API -> "/files/my/$path"
                FileRepository.CONTEXT_COURSES -> "/files/courses/${parts[1]}$path"
                else -> null
            }
        }

    override fun provideSelfConfig() = viewModel.course
            .combineLatestBothNullable(viewModel.currentUser)
            .map { (course, user) ->
                breadcrumbs.setPath(args.path, course)
                val parts = args.path.getPathParts()

                MainFragmentConfig(
                        title = when {
                            parts.size > 2 -> parts.last()
                            parts.first() == FileRepository.CONTEXT_MY_API ->
                                context?.getString(R.string.file_directory_my)
                            parts.first() == FileRepository.CONTEXT_COURSES ->
                                course?.name ?: context?.getString(R.string.file_directory_course_unknown)
                            else -> context?.getString(R.string.file_directory_unknown)
                        },
                        showTitle = false,
                        toolbarColor = course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = listOf(R.menu.fragment_file_bottom),
                        menuBottomHiddenIds = listOf(
                                R.id.file_action_gotoCourse.takeIf { course == null }
                        ),
                        fabIconRes = R.drawable.ic_file_upload_white_24dp,
                        fabVisible = user.hasPermission(Permission.FILE_CREATE)
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
            val directoriesEmpty = viewModel.directories.value?.isEmpty() ?: true
            val filesEmpty = viewModel.files.value?.isEmpty() ?: true

            empty.visibilityBool = directoriesEmpty && filesEmpty
            directoriesHeader.visibilityBool = !directoriesEmpty
            filesHeader.visibilityBool = !filesEmpty
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
        breadcrumbs.onPathSelected = callback@{ path ->
            if (path == args.path) {
                performRefresh()
                return@callback
            }

            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(path).build().toBundle())
        }
        mainViewModel.toolbarColors.observe(this, Observer {
            breadcrumbs.textColor = it.textColor
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.file_action_gotoCourse -> viewModel.courseId?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onFabClicked() {
        launch(UI) {
            if (!requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getContext()!!.showGenericError(R.string.file_fileUpload_error_readPermissionDenied)
                return@launch
            }

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, REQUEST_FILE_TO_UPLOAD)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_FILE_TO_UPLOAD) {
            // Probably cancelled by user -> don't show error message
            if (resultCode != Activity.RESULT_OK) return

            val uri = data?.data
            val fileReadInfo = uri?.let { context!!.prepareFileRead(it) }
            if (fileReadInfo == null) {
                context!!.showGenericError(R.string.file_fileUpload_error_read)
                return
            }

            launch(UI) {
                getContext()!!.withProgressDialog(R.string.file_fileUpload_progress) {
                    val res = FileRepository.upload(viewModel.path, fileReadInfo.name, fileReadInfo.size) {
                        fileReadInfo.streamGenerator().also {
                            if (it == null)
                                getContext()!!.showGenericError(R.string.file_fileUpload_error_read)
                        }
                    }
                    if (!res) getContext()!!.showGenericError(R.string.file_fileUpload_error_upload)

                    FileRepository.syncDirectory(viewModel.path)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override suspend fun refresh() {
        FileRepository.syncDirectory(viewModel.path)
        viewModel.courseId?.also {
            CourseRepository.syncCourse(it)
        }
        UserRepository.syncCurrentUser()
    }


    @Suppress("ComplexMethod")
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
                    this@FileFragment.context?.showGenericSuccess(
                            getString(R.string.file_fileDownload_success, file.name))
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(response.url), response.header?.contentType)
                }
                val packageManager = activity?.packageManager
                if (packageManager != null && intent.resolveActivity(packageManager) != null)
                    startActivity(intent)
                else
                    this@FileFragment.context?.showGenericError(
                            getString(R.string.file_fileOpen_error_cantResolve, file.name?.fileExtension))
            }
        } catch (e: HttpException) {
            @Suppress("MagicNumber")
            when (e.code()) {
                404 -> this@FileFragment.context?.showGenericError(R.string.file_fileOpen_error_404)
            }
        }
    }
}
