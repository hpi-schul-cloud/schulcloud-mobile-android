package org.schulcloud.mobile.controllers.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseFragment
import org.schulcloud.mobile.viewmodels.ChooseFileViewmodel

class ChooseFileFragment: BaseFragment() {
    private val directoryAdapter by lazy {
        DirectoryAdapter{

        }
    }

    private val fileAdapter by lazy {
        DirectoryAdapter{

        }
    }

    private lateinit var mViewModel: ChooseFileViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel = ViewModelProviders.of(this).get(ChooseFileViewmodel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_file,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun returnFilePath(path: String){

    }
}
