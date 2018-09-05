package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.lifecycle.*
import com.google.android.material.tabs.TabLayout
import org.schulcloud.mobile.R
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.views.LiveViewPager


abstract class TabbedMainFragment<F : MainFragment<F, VM>, VM : ViewModel> : MainFragment<F, VM>() {
    companion object {
        val TAG = TabbedMainFragment::class.simpleName
    }

    abstract val pagerAdapter: MainPagerAdapter<F, VM>
    lateinit var pager: LiveViewPager
        private set
    var tabLayout: TabLayout? = null
        private set

    final override val currentInnerFragment: LiveData<Int?>
    private val currentPositionAddFunc: (LiveData<Int>) -> Unit
    override val innerFragments: List<InnerMainFragment<*, F, VM>?>
        get() = pagerAdapter.fragments.value ?: ArrayList(pagerAdapter.count)

    init {
        val (position, addFunc) = zipLater<Int>()
        currentInnerFragment = position.map { it }
        currentPositionAddFunc = addFunc
    }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager = view.findViewById(R.id.viewPager)
        // Subscribe currentInnerFragment to pager position
        currentPositionAddFunc(pager.currentItemLiveData)
        pager.adapter = pagerAdapter

        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout?.setupWithViewPager(pager)

        mainViewModel.toolbarColors.observe(this, Observer {
            tabLayout?.setTabTextColors(it.textColorSecondary, it.textColor)
            tabLayout?.setSelectedTabIndicatorColor(it.textColor)
        })
    }

    final override fun provideConfig(): LiveData<MainFragmentConfig> {
        var lastFragment: InnerMainFragment<*, F, VM>? = null
        var pos = 0

        val (result, addFunc) = switch<MainFragmentConfig?>()
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (currentInnerFragment.value ?: 0 != pos)
                    return

                @Suppress("UNCHECKED_CAST")
                addFunc(lastFragment?.config as LiveData<MainFragmentConfig?> ?: liveDataOf())
            }
        }

        return provideConfig(currentInnerFragment
                .combineLatest(pagerAdapter.fragments)
                .switchMapNullable { (position, fragments) ->
                    pos = position ?: 0

                    val innerFragment = fragments.getOrNull(pos)
                    if (innerFragment != null) {
                        // Remove observer from old fragment
                        lastFragment?.let { lifecycle.removeObserver(observer) }

                        // Add to new fragment
                        lastFragment = innerFragment
                        // workaround as fragments are not fully initialized when tab is switched
                        innerFragment.getLifecycle().removeObserver(observer)
                        innerFragment.getLifecycle().addObserver(observer)
                        result
                    } else
                        null
                })
    }

    protected open fun provideConfig(selectedTabConfig: LiveData<MainFragmentConfig?>)
            : LiveData<MainFragmentConfig> = selectedTabConfig.filterNotNull()
}
