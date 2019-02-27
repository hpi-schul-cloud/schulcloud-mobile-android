package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.*
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.topic.TopicRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private val course = course(ID)
private val topicList = org.schulcloud.mobile.topicList(5)
private lateinit var mockRealm: Realm

object CourseViewModelSpec : Spek({
    describe("A courseViewModel") {
        val courseViewModel by memoized {
            CourseViewModel(ID)
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(CourseRepository)
            every { CourseRepository.course(mockRealm, ID) } returns course.asLiveData()
            mockkObject(TopicRepository)
            every { TopicRepository.topicsForCourse(mockRealm, ID) } returns topicList.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkAll()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(course, courseViewModel.course.value)
                assertEquals(topicList, courseViewModel.topics.value)
            }
        }
    }
})
