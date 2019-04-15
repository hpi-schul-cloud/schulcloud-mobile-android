package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.*
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object CourseViewModelSpec : Spek({
    val id = "id"
    val course = course(id)
    val topicList = topicList(5)

    describe("A courseViewModel") {
        val courseViewModel by memoized {
            CourseViewModel(id)
        }
        val mockRealm = mockk<Realm>()
        mockRealmDefaultInstance(mockRealm)
        mockkObject(CourseRepository)
        mockkObject(TopicRepository)

        beforeEach {
            prepareTaskExecutor()
            every { CourseRepository.course(mockRealm, id) } returns course.asLiveData()
            every { TopicRepository.topicsForCourse(mockRealm, id) } returns topicList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(course, courseViewModel.course.value)
                assertEquals(topicList, courseViewModel.topics.value)
            }
        }
    }
})
