package org.schulcloud.mobile.viewmodels

import android.graphics.Color
import android.view.MenuItem
import androidx.lifecycle.Observer
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


object MainViewModelSpec : Spek({
    val color = Color.BLACK
    val statusBarColor = Color.BLUE
    val textColor = Color.CYAN
    val textColorSecondary = Color.GRAY
    val toolbarColors = ToolbarColors(color, statusBarColor, textColor, textColorSecondary)
    val title = "title"
    val configTitle = "configTitle"
    val menuItem = mockk<MenuItem>()
    val config = MainFragmentConfig(title = configTitle)

    describe("A mainViewModel") {
        val mainViewModel by memoized {
            MainViewModel()
        }
        val configObserver = spyk<Observer<MainFragmentConfig>>()
        val titleObserver = spyk<Observer<String>>()
        val toolbarColorsObserver = spyk<Observer<ToolbarColors>>()
        val onOptionsItemSelectedObserver = spyk<Observer<MenuItem>>()
        val onFabClickedObserver = spyk<Observer<Void>>()

        beforeEach {
            prepareTaskExecutor()
        }

        afterEach {
            resetTaskExecutor()
        }

        describe("data changes") {
            describe("config changes"){
                beforeEach {
                    mainViewModel.config.observeForever(configObserver)
                    mainViewModel.config.value = config
                }
                it("should return the correct config") {
                    verify { configObserver.onChanged(config) }
                }
            }
            describe("title changes"){
                beforeEach {
                    mainViewModel.title.observeForever(titleObserver)
                    mainViewModel.title.value = title
                }
                it("should return the correct title") {
                    verify { titleObserver.onChanged(title) }
                }
            }
            describe("toolbarColors changes"){
                beforeEach {
                    mainViewModel.toolbarColors.observeForever(toolbarColorsObserver)
                    mainViewModel.toolbarColors.value = toolbarColors
                }
                it("should return the correct toolbarColors") {
                    verify { toolbarColorsObserver.onChanged(toolbarColors) }
                }
            }
            describe("onOptionsItemSelected changes"){
                beforeEach {
                    mainViewModel.onOptionsItemSelected.observeForever(onOptionsItemSelectedObserver)
                    mainViewModel.onOptionsItemSelected.value = menuItem
                }
                it("should return the correct MenuItem") {
                    verify { onOptionsItemSelectedObserver.onChanged(menuItem) }
                }
            }
            describe("onFabClicked changes"){
                beforeEach {
                    mainViewModel.onFabClicked.observeForever(onFabClickedObserver)
                    mainViewModel.onFabClicked.call()
                }
                it("should notify the observer") {
                    verify { onFabClickedObserver.onChanged(null) }
                }
            }
        }
    }
})
