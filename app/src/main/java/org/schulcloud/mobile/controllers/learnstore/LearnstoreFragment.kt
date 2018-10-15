package org.schulcloud.mobile.controllers.learnstore


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_learnstore.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.main.FragmentType
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig

import org.schulcloud.mobile.models.material.MaterialRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.MaterialListViewModel

class LearnstoreFragment : MainFragment<MaterialListViewModel>() {

    companion object {
        val TAG: String = LearnstoreFragment::class.java.simpleName
    }

    private val materialAdapter: MaterialListAdapter by lazy {
        MaterialListAdapter()
    }

    override var url: String? = "/content"
    override fun provideConfig(): LiveData<MainFragmentConfig> = MainFragmentConfig(
            fragmentType = FragmentType.PRIMARY,
            title = "Lernstore"
    ).asLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MaterialListViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_learnstore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        materialAdapter.emptyIndicator = empty
        viewModel.materials.observe(this, Observer {
            materialAdapter.update(it ?: emptyList())
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = materialAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override suspend fun refresh() {
        MaterialRepository.syncMaterials()
    }
}
