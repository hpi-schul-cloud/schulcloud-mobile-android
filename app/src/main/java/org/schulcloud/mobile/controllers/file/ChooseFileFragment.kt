package org.schulcloud.mobile.controllers.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_choose_file.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.ChooseFileViewmodel

class ChooseFileFragment: MainFragment<ChooseFileViewmodel>() {

    override suspend fun refresh() {}

    override fun provideConfig(): LiveData<MainFragmentConfig> = MainFragmentConfig(
            title = resources.getString(R.string.upload_file),
            subtitle = mViewModel.path,
            toolbarColor = resources.getColor(R.color.brand_hpiRed)
    ).asLiveData()

    private val directoryAdapter by lazy {
        DirectoryAdapter{
            navController.navigate(R.layout.fragment_choose_file)
        }
    }

    private val fileAdapter by lazy {
        FileAdapter({returnFilePath(it.path!!)},{select(it.path!!)})
    }

    private val args: FileFragmentArgs by lazy{
        FileFragmentArgs.fromBundle(arguments)
    }

    private lateinit var mViewModel: ChooseFileViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(this).get(ChooseFileViewmodel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_file,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        back.setOnClickListener({l -> returnFilePath("")})
        val path = savedInstanceState!!.get("path") as String
        updatePath(path)
        super.onViewCreated(view, savedInstanceState)
    }

    fun updatePath(path: String){
        val file = java.io.File(path)

        var files: MutableList<File> = mutableListOf()
        var directories: MutableList<Directory> = mutableListOf()

        if(!file.exists()) {
            return
        }

        file.list().forEach {
            if(it.contains(".")){
                val name = it.split(".")[0]
                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.split(".")[1])
                val scloudFile = File()
                scloudFile.name = name
                scloudFile.type = type
                files.add(scloudFile)
            }else{
                val scloudDirectory = Directory()
                scloudDirectory.name = it
                scloudDirectory.path = file.path + it
            }
        }

        mViewModel.directories = directories
        mViewModel.files = files
        mViewModel.path = path
        fileAdapter.update(mViewModel.files)
        directoryAdapter.update(mViewModel.directories)
    }

    fun select(path: String){
        val file = java.io.File(path)

        if(!file.exists())
            return

        if(file.isDirectory){
            updatePath(file.path)
        }else{
            //highlight the file?
        }
    }

    fun returnFilePath(path: String){

    }

}
