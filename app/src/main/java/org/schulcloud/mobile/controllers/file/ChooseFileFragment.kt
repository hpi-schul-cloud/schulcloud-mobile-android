package org.schulcloud.mobile.controllers.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_choose_file.*
import okhttp3.MediaType
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.viewmodels.ChooseFileViewmodel

class ChooseFileFragment: BaseFragment() {
    private val directoryAdapter by lazy {
        DirectoryAdapter{}
    }

    private val fileAdapter by lazy {
        FileAdapter(,)
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
        super.onViewCreated(view, savedInstanceState)
    }

    fun updatePath(path: String){
        val file = java.io.File(path)

        var contents: Array<String> = arrayOf()
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
