package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.courseList
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CourseListViewModelSpec : Spek({
    val courses = courseList(5)

    describe("A courseListViewModel") {
        val courseListViewModel by memoized {
            CourseListViewModel()
        }

        val mockRealm = mockk<Realm>()
        mockkObject(CourseRepository)
        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { CourseRepository.courses(mockRealm) } returns courses.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(courses, courseListViewModel.courses.value)
            }
        }
    }
})
