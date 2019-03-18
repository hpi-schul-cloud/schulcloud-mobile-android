package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.schulcloud.mobile.commonTest.topic
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object TopicViewModelSpec : Spek({
    val id = "id"
    val courseId = "courseId"
    val topic = topic(id)
    val course = course(courseId)

    describe("A topicViewModel") {
        val topicViewModel by memoized {
            TopicViewModel(id)
        }
        val mockRealm = mockk<Realm>()
        mockkObject(TopicRepository)
        mockkObject(CourseRepository)
        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { TopicRepository.topic(mockRealm, id) } returns topic.asLiveData()
            every { CourseRepository.course(mockRealm, courseId) } returns course.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(topic, topicViewModel.topic.value)
                assertEquals(course, topicViewModel.course(courseId).value)
            }
        }
    }
})
