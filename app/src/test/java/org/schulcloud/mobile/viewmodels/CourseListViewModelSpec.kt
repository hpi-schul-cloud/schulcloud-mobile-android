package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.courseList
import org.schulcloud.mobile.mockRealmDefaultInstance
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val courses = courseList(5)
private lateinit var mockRealm: Realm

object CourseListViewModelSpec : Spek({
    describe("A courseListViewModel") {
        val courseListViewModel by memoized {
            CourseListViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(CourseRepository)
            every { CourseRepository.courses(mockRealm) } returns courses.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkAll()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(courses, courseListViewModel.courses.value)
            }
        }
    }
})
