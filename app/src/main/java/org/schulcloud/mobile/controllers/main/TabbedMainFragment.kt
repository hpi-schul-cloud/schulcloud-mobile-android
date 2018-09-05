package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.mutableLiveDataOf


abstract class TabbedMainFragment<F : MainFragment<F, VM>, VM : ViewModel> : MainFragment<F, VM>() {
    abstract val pagerAdapter: MainPagerAdapter<F, VM>
    lateinit var pager: ViewPager
        private set
    var tabLayout: TabLayout? = null
        private set

    override val currentInnerFragment: MutableLiveData<Int?> = mutableLiveDataOf()
    override val innerFragments: List<InnerMainFragment<*, F, VM>>
        get() = pagerAdapter.tabs.map {
            @Suppress("UNCHECKED_CAST")
            it.fragment as InnerMainFragment<*, F, VM>
        }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager = view.findViewById(R.id.viewPager)
        pager.adapter = pagerAdapter
        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout?.setupWithViewPager(pager)

        mainViewModel.toolbarColors.observe(this, Observer {
            tabLayout?.setTabTextColors(it.textColorSecondary, it.textColor)
            tabLayout?.setSelectedTabIndicatorColor(it.textColor)
        })

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                currentInnerFragment.value = position
            }
        })
    }
}
