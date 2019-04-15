package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.Observer
import io.mockk.*
import io.realm.Realm
import io.realm.RealmList
import org.schulcloud.mobile.commonTest.*
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.homework.HomeworkCourse
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object HomeworkViewModelSpec : Spek({
    val id = "id"
    val courseId = "courseId"
    val userId = "userId"
    val submissionId = "submissionId"
    val userList = userList(5)
    val course = course(courseId).apply {
        this.users = RealmList<User>().apply{
            addAll(userList)
        }
    }
    val homework = homework(id).apply {
        this.course = HomeworkCourse().apply {
            this.id = courseId
        }
    }
    val submissionList = submissionListWithStudents(4)
    val expectedSubmissions = mutableListOf<Pair<User, Submission?>>().apply {
        for (i in 0..3) {
            add(Pair(userList[i], submissionList[i]))
        }
        add(Pair(userList[4], null))
    }
    val submission = submission(submissionId)

    describe("A homeworkViewModel") {
        val homeworkViewModel by memoized {
            HomeworkViewModel(id)
        }
        val mockRealm = mockk<Realm>()
        mockRealmDefaultInstance(mockRealm)
        val observer = spyk<Observer<List<Pair<User, Submission?>>>>()
        mockkObject(HomeworkRepository)
        mockkObject(CourseRepository)
        mockkObject(SubmissionRepository)
        mockkStatic(UserRepository::class)

        beforeEach {
            prepareTaskExecutor()
            every { HomeworkRepository.homework(mockRealm, id) } returns homework.asLiveData()
            every { CourseRepository.course(mockRealm, courseId) } returns course.asLiveData()
            every { SubmissionRepository.submissionsForHomework(mockRealm, id) } returns submissionList.asLiveData()
            every { SubmissionRepository.submission(mockRealm, id, userId) } returns submission.asLiveData()
            every { UserRepository.userId } returns userId
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            beforeEach {
                homeworkViewModel.submissions.observeForever(observer)
            }

            it("should return the correct data") {
                assertEquals(homework, homeworkViewModel.homework.value)
                assertEquals(course, homeworkViewModel.course.value)
                assertEquals(expectedSubmissions, homeworkViewModel.submissions.value)
                assertEquals(submission, homeworkViewModel.mySubmission.value)
            }
        }
    }
})
