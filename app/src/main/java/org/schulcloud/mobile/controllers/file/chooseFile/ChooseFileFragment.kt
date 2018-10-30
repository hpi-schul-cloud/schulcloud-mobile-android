package org.schulcloud.mobile.controllers.file.chooseFile

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_choose_file.*
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.android.synthetic.main.item_file_upload.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.file.DirectoryAdapter
import org.schulcloud.mobile.controllers.file.FileAdapter
import org.schulcloud.mobile.controllers.file.FileFragment
import org.schulcloud.mobile.controllers.file.FileFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.SignedUrlRequest
import org.schulcloud.mobile.models.file.SignedUrlResponse
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.files.FileService
import org.schulcloud.mobile.utils.NotificationUtils
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.showGenericError
import org.schulcloud.mobile.viewmodels.ChooseFileViewmodel
import ru.gildor.coroutines.retrofit.await
import java.util.*

class ChooseFileFragment: MainFragment<ChooseFileViewmodel>() {

    override suspend fun refresh() {
        updatePath(mViewModel.path)
    }

    override fun provideConfig(): LiveData<MainFragmentConfig> = MainFragmentConfig(
            title = resources.getString(R.string.upload_file),
            subtitle = mViewModel.path,
            toolbarColor = resources.getColor(R.color.brand_hpiRed)
    ).asLiveData()

    private val directoryAdapter by lazy {
        DirectoryAdapter {
            updatePath(mViewModel.path + it + "/")
      }
    }

    private val fileAdapter by lazy {
        FileAdapter({ select(it)}, {returnToFiles(it.path!!,it.name!!)})
    }

    private val args: ChooseFileFragmentArgs by lazy{
        ChooseFileFragmentArgs.fromBundle(arguments)
    }

    private lateinit var mViewModel: ChooseFileViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(this).get(ChooseFileViewmodel::class.java)
        mViewModel.path = args.localPath
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBindingView = DataBindingUtil.inflate<ViewDataBinding>(inflater,R.layout.fragment_choose_file,container?.parent as ViewGroup?,false)
        val view = dataBindingView.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_main.setOnClickListener {
            colorFile(java.io.File(""))
        }
        mainActivity.onBackAction = {
            if(mViewModel.path != resources.getString(R.string.base_path_storage)) {
                val prevPathLength = mViewModel.path.length - mViewModel.path.split("/")[mViewModel.path.split("/").lastIndex - 1].length - 1
                val prevPath = mViewModel.path.substring(0, prevPathLength)

                updatePath(prevPath)
            }
        }
        chooseDirectory_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = directoryAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        chooseFile_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fileAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        updatePath(mViewModel.path)
    }

    fun updatePath(path: String){
        val file = java.io.File(path)

        var files: MutableList<File> = mutableListOf()
        var directories: MutableList<Directory> = mutableListOf()

        if(!file.exists()) {
            return
        }

        if(file.list() != null) {
            empty_directory.visibility=View.GONE
            file.list().forEach {
                if (it.contains(".") && it.indexOf('.') != 0) {
                    val name = it
                    val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.split(".")[1])
                    val scloudFile = File()
                    val file = java.io.File(path + it + "/")
                    scloudFile.name = name
                    scloudFile.type = type
                    scloudFile.size = file.length()
                    scloudFile.path = file.path
                    files.add(scloudFile)
                } else {
                    if (it.indexOf('.') != 0) {
                        val scloudDirectory = Directory()
                        scloudDirectory.name = it
                        scloudDirectory.path = file.path + '/' + it
                        directories.add(scloudDirectory)
                    }
                }
            }
        }else{
            empty_directory.visibility=View.VISIBLE
        }

        mViewModel.directories = directories
        mViewModel.files = files
        mViewModel.path = path
        mainActivity.supportActionBar?.subtitle = mViewModel.path.substring(mViewModel.normalLength)
        fileAdapter.update(mViewModel.files)
        directoryAdapter.update(mViewModel.directories)
    }

    fun select(scloudfile: File){
        val file = java.io.File(scloudfile.path)

        if(!file.exists())
            return

        if(file.isDirectory){
            updatePath(file.path)
        }else{
            colorFile(file)
        }
    }

    fun colorFile(file: java.io.File){
        for(i in 0 until fileAdapter.itemCount){
            if(fileAdapter.viewHolders.get(i).binding.name.text == file.name){
                fileAdapter.viewHolders.get(i).binding.root.setBackgroundColor(resources.getColor(R.color.brand_hpiYellow))
            }else{
                fileAdapter.recyclerView?.get(i)?.setBackgroundColor(Color.parseColor("#ffffff"))
            }
        }
    }

    private fun returnToFiles(filePath: String,fileName: String){
        uploadFile(filePath,args.path + fileName)
        navController.popBackStack()
    }

    private fun uploadFile(filepath: String,userPath: String) = launch(UI){
        if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED){
            this@ChooseFileFragment.context?.showGenericError(resources.getString(R.string.oops_something_went_wrong))
            return@launch
        }

        val notificationId = Random().nextInt()

        val cancelIntent = Intent(this@ChooseFileFragment.context, FileFragment.downloadBroadcastReceiver::class.java)
                .putExtra("notificationId",notificationId)

        val notification = NotificationCompat.Builder(this@ChooseFileFragment.context!!, NotificationUtils.channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(this@ChooseFileFragment.resources.getString(R.string.notification_cloud))
                .setContentText(this@ChooseFileFragment.resources.getString(R.string.file_is_uploading))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDeleteIntent(PendingIntent.getBroadcast(this@ChooseFileFragment.context,0,cancelIntent,0))
                .setProgress(0,0,true)
                .build()

        NotificationManagerCompat.from(this@ChooseFileFragment.context!!).notify(notificationId,notification)

        var file = java.io.File(filepath)
        var responseUrl: SignedUrlResponse? = null
        val callback: (success: Boolean) -> Unit = {
            if(it) {
                this@ChooseFileFragment.directoryAdapter.notifyDataSetChanged()
                this@ChooseFileFragment.fileAdapter.notifyDataSetChanged()
            }
            NotificationManagerCompat.from(this@ChooseFileFragment.context!!).cancel(notificationId)
        }

        try{
            responseUrl = ApiService.getInstance().generateSignedUrl(SignedUrlRequest().apply {
                action = SignedUrlRequest.ACTION_PUT
                path = userPath
                fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            }).await()

            FileService.uploadFile(file,responseUrl,callback)
        }catch(e: Exception){
            Log.i(FileFragment.TAG,e.message)
            return@launch
        }
    }
}
