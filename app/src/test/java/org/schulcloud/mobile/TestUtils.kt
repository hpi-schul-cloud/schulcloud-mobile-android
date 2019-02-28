package org.schulcloud.mobile

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import io.mockk.every
import io.mockk.mockkStatic
import io.realm.Realm

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
