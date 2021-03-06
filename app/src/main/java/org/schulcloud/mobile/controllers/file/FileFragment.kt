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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentFileBinding
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.FileViewModelFactory
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await


class FileFragment : MainFragment<FileViewModel>() {
    companion object {
        val TAG: String = FileFragment::class.java.simpleName
    }

    private val directoryAdapter: DirectoryAdapter by lazy {
        DirectoryAdapter { refOwnerModel, owner, parent ->
            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(refOwnerModel, owner, parent).build().toBundle())
        }
    }
    private val fileAdapter: FileAdapter by lazy {
        FileAdapter({ loadFile(it, false) },
                { loadFile(it, true) })
    }


    override var url: String? = null
        get() {
            val path = idPathParts.combinePath()
            return when (args.refOwnerModel) {
                FileRepository.CONTEXT_MY_API -> "/files/my/$path"
                FileRepository.CONTEXT_COURSE -> "/files/courses/${args.owner}/$path"
                else -> null
            }
        }

    private val args: FileFragmentArgs by navArgs()
    override fun provideConfig() = (getCourseFromFolder()?.let {
        CourseRepository.course(viewModel.realm, it)
    } ?: null.asLiveData<Course>())
            .map { course ->
                breadcrumbs.setPath(namePathParts, args.refOwnerModel, args.owner,  args.parent, course)

                MainFragmentConfig(
                        title = when {
                            args.parent != null -> viewModel.directory((args.parent).toString())?.name
                            args.refOwnerModel == FileRepository.CONTEXT_MY_API ->
                                context?.getString(R.string.file_directory_my)
                            args.refOwnerModel == FileRepository.CONTEXT_COURSE ->
                                course?.name ?: context?.getString(R.string.file_directory_course_unknown)
                            else -> context?.getString(R.string.file_directory_unknown)
                        },
                        showTitle = false,
                        toolbarColor = course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = R.menu.fragment_file_bottom,
                        menuBottomHiddenIds = listOf(
                                if (course == null) R.id.file_action_gotoCourse else 0
                        )
                )
            }

    private val namePathParts: List<String?>
        get() = getDirectoryPathParts(args.parent, true)

    private val idPathParts: List<String?>
        get() = getDirectoryPathParts(args.parent)


    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, FileViewModelFactory(args.owner, args.parent))
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

        breadcrumbs.setPath(namePathParts, args.refOwnerModel, args.owner, args.parent)
        breadcrumbs.onPathSelected = callback@{ refOwnerModel, owner, parent ->
            if (refOwnerModel == args.refOwnerModel && owner == args.owner && parent == args.parent) {
                performRefresh()
                return@callback
            }

            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(refOwnerModel, owner, parent).build().toBundle())
        }
        mainViewModel.toolbarColors.observe(this, Observer {
            breadcrumbs.setTextColor(it.textColor)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.file_action_gotoCourse -> getCourseFromFolder()?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override suspend fun refresh() {
        FileRepository.syncDirectory(viewModel.owner, viewModel.parent)
        FileRepository.syncDirectoriesForOwner(viewModel.owner)
        getCourseFromFolder()?.also {
            CourseRepository.syncCourse(it)
        }
    }


    private fun getCourseFromFolder(): String? {
        if (args.refOwnerModel != FileRepository.CONTEXT_COURSE)
            return null

        return args.owner
    }

    private fun getDirectoryPathParts(directoryId: String?, isNamePath: Boolean = false): List<String?> {
        val pathParts = mutableListOf<String?>()
        var currentId: String? = directoryId
        var currentDirectory: File?
        while (currentId != null){
            currentDirectory = viewModel.directory(currentId)
            pathParts.add(0, if (isNamePath) currentDirectory?.name else currentDirectory?.id)
            currentId = currentDirectory?.parent
        }
        return pathParts.toList()
    }


    @Suppress("ComplexMethod")
    private fun loadFile(file: File, download: Boolean) = launch(Dispatchers.Main) {
        try {
            val response = ApiService.getInstance().generateSignedUrl(file.id, download).await()

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
                    setDataAndType(Uri.parse(response.url), file.type)
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
