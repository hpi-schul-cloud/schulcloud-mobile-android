package org.schulcloud.mobile.controllers.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel


abstract class MainPagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(val fm: FragmentManager) :
        FragmentPagerAdapter(fm) {
    abstract val tabs: List<Tab<*, P, VM>>
}

class SimplePagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(
    fm: FragmentManager,
    override val tabs: List<Tab<*, P, VM>>
) : MainPagerAdapter<P, VM>(fm) {

    override fun getItem(position: Int): Fragment? {
        return if (position in tabs.indices) tabs[position].fragment else null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position in tabs.indices) tabs[position].title else null
    }

    override fun getCount() = tabs.size
}

fun <P : MainFragment<P, VM>, VM : ViewModel> List<Tab<*, P, VM>>.toPagerAdapter(fragment: P): MainPagerAdapter<P, VM> {
    return SimplePagerAdapter(fragment.childFragmentManager, this)
}

class LiveDataPagerAdapter<P : MainFragment<P, VM>, VM : ViewModel>(
    val fragment: P,
    tabsLiveData: LiveData<List<Tab<*, P, VM>>>
) : MainPagerAdapter<P, VM>(fragment.childFragmentManager) {

    override var tabs: List<Tab<*, P, VM>> = emptyList()

    init {
        tabsLiveData.observe(fragment, Observer {
            tabs = it ?: emptyList()
            notifyDataSetChanged()
        })
    }

    override fun getItem(position: Int): Fragment? {
        return if (position in tabs.indices) tabs[position].fragment else null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position in tabs.indices) tabs[position].title else null
    }

    override fun getCount() = tabs.size
}

fun <P : MainFragment<P, VM>, VM : ViewModel> LiveData<List<Tab<*, P, VM>>>.toPagerAdapter(fragment: P): MainPagerAdapter<P, VM> {
    return LiveDataPagerAdapter(fragment, this)
}


data class Tab<F : InnerMainFragment<F, P, VM>, P : MainFragment<P, VM>, VM : ViewModel>(
    val title: String,
    val fragment: F
)
