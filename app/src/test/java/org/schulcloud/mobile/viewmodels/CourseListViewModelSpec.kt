package org.schulcloud.mobile.viewmodels

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import io.realm.Realm
import org.junit.Rule
import org.junit.rules.TestRule
import org.schulcloud.mobile.courseList
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val courses = courseList(5)
lateinit var mockRealm: Realm
object CourseListViewModelSpec : Spek({

    describe("A courseListViewModel") {
        val courseListViewModel by memoized {
            CourseListViewModel()
        }

        beforeEachTest {
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

            mockRealm = mockk()
            mockkStatic(Realm::class)
            every { Realm.getDefaultInstance() } returns mockRealm

            mockkObject(CourseRepository)
            every { CourseRepository.courses(mockRealm) } returns courses.asLiveData()
        }

        afterEach {
            ArchTaskExecutor.getInstance().setDelegate(null)
            unmockkObject(CourseRepository)
            unmockkStatic(Realm::class)
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(courses, courseListViewModel.courses.value)
            }
        }
    }
})
