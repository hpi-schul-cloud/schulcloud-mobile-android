package org.schulcloud.mobile.controllers.file

import android.Manifest
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.dialog_add_directory.view.*
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.ResponseBody
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentFileBinding
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.files.FileService
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import retrofit2.HttpException
import ru.gildor.coroutines.retrofit.await
import java.util.*


class FileFragment : MainFragment<FileViewModel>() {
    companion object {
        val TAG: String = FileFragment::class.java.simpleName
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

    override fun provideConfig() = (getCourseFromFolder()?.let {
        CourseRepository.course(viewModel.realm, it)
    } ?: null.asLiveData<Course>())
            .map { course ->
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

        files_add_folder.setOnClickListener {
            openDirectoryDialog()
        }

        files_upload_file.setOnClickListener{
            openSelectFileDialog()
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
            breadcrumbs.setTextColor(it.textColor)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.file_action_gotoCourse -> getCourseFromFolder()?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
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
        if (!args.path.startsWith(FileRepository.CONTEXT_COURSES))
            return null

        return args.    path.getPathParts()[1]
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

                val notificationId = Random().nextInt()
                val notification = NotificationCompat.Builder(this@FileFragment.context!!,NotificationUtils.channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(this@FileFragment.resources.getString(R.string.notification_cloud))
                        .setContentText(this@FileFragment.resources.getString(R.string.notification_description))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build()

                NotificationManagerCompat.from(this@FileFragment.context!!).notify(notificationId,notification)

               // FileService.downloadFile(response,)

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

    fun openDirectoryDialog(){
        viewModel.user.observe(this, Observer {
            if(!it?.hasPermission(User.PERMISSION_FOLDER_CREATE)!!){
                Toast.makeText(context,resources.getString(R.string.file_directoryCreate_permission_error),Toast.LENGTH_SHORT).show()
            }else{
                addDirectoryDialog(viewModel.path,Runnable{async{this@FileFragment.refresh()}}).show(activity?.supportFragmentManager,"addDirectoryDialog")
            }
        })
    }

    fun openSelectFileDialog(){
    }

    class addDirectoryDialog(private val path: String, val refresh: Runnable): DialogFragment(){

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            val inflater: LayoutInflater = activity!!.layoutInflater
            val view: View = inflater.inflate(R.layout.dialog_add_directory,null)
            var layoutParamsDialog = ActionBar.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParamsDialog.rightMargin = 10

            builder.setView(view)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        if(!view.directory_name.text.isNullOrBlank()){
                            var request: Directory = Directory()
                            request.path = combinePath(path,view.directory_name.text.toString())
                            async{
                                FileRepository.createDirectory(request)
                                refresh.run()
                            }
                            dismiss()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, which ->
                        dismiss()
                    }
            return builder.create()
        }
    }
}
