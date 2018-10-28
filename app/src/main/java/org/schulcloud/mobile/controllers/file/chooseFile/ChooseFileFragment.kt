package org.schulcloud.mobile.controllers.file.chooseFile

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_choose_file.*
import kotlinx.android.synthetic.main.fragment_file.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.file.DirectoryAdapter
import org.schulcloud.mobile.controllers.file.FileAdapter
import org.schulcloud.mobile.controllers.file.FileFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.ChooseFileViewmodel

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
        FileAdapter({ returnFilePath(it.path!!) }, { select(it.path!!) })
    }

    private val args: FileFragmentArgs by lazy{
        FileFragmentArgs.fromBundle(arguments)
    }

    private lateinit var mViewModel: ChooseFileViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(this).get(ChooseFileViewmodel::class.java)
        val path = savedInstanceState?.getString("path")
        //mViewModel.path = path!!
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBindingView = DataBindingUtil.inflate<ViewDataBinding>(inflater,R.layout.fragment_choose_file,container?.parent as ViewGroup?,false)
        val view = dataBindingView.root
        mainActivity.setSupportActionBar(view.findViewById(R.id.toolbar))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
