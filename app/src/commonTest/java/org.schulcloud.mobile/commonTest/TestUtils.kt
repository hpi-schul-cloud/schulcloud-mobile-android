package org.schulcloud.mobile.commonTest

import android.app.Instrumentation
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.test.platform.app.InstrumentationRegistry
import org.schulcloud.mobile.controllers.settings.SettingsActivity

fun prepareTaskExecutor() {
    // In order to test LiveData, the `InstantTaskExecutorRule` rule needs to be applied via JUnit.
    // As we are running it with Spek, the "rule" will be implemented in this way instead
    // https://github.com/spekframework/spek/issues/337#issuecomment-396000505
    ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
        override fun executeOnDiskIO(runnable: Runnable) {
            runnable.run()
        }

        override fun isMainThread(): Boolean {
            return true
        }

        override fun postToMainThread(runnable: Runnable) {
            runnable.run()
        }
    })
}

fun resetTaskExecutor() {
    ArchTaskExecutor.getInstance().setDelegate(null)
}

fun createAndAddIntentBlockingActivityMonitor(activityName: String): Instrumentation.ActivityMonitor{
    val activityMonitor = Instrumentation.ActivityMonitor(activityName, null, true)
    InstrumentationRegistry.getInstrumentation().addMonitor(activityMonitor)
    return activityMonitor
}

fun checkActivityStarted(activityMonitor: Instrumentation.ActivityMonitor): Boolean
        = InstrumentationRegistry.getInstrumentation().checkMonitorHit(activityMonitor, 1)
