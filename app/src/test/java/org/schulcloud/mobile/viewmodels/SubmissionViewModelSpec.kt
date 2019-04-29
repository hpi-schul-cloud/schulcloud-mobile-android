package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.Observer
import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.*
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object SubmissionViewModelSpec : Spek({
    val id = "id"
    val userId = "userId"
    val homeworkId = "homeworkId"
    val submission = submission(id).apply {
        studentId = userId
        this.homeworkId = homeworkId
    }
    val user = user(userId)
    val homework = homework(homeworkId)

    describe("A submissionViewModel") {
        val submissionViewModel by memoized {
            SubmissionViewModel(id)
        }
        val mockRealm = mockk<Realm>()
        val studentObserver = spyk<Observer<User?>>()
        val homeworkObserver = spyk<Observer<Homework?>>()

        beforeGroup {
            mockRealmDefaultInstance(mockRealm)
            mockkObject(SubmissionRepository)
            mockkObject(UserRepository)
            mockkObject(HomeworkRepository)
        }

        beforeEach {
            prepareTaskExecutor()
            every { SubmissionRepository.submission(mockRealm, id) } returns submission.asLiveData()
            every { UserRepository.user(mockRealm, userId) } returns user.asLiveData()
            every { HomeworkRepository.homework(mockRealm, homeworkId) } returns homework.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            beforeEach {
                submissionViewModel.student.observeForever(studentObserver)
                submissionViewModel.homework.observeForever(homeworkObserver)
            }

            it("should return the correct data") {
                assertEquals(submission, submissionViewModel.submission.value)
                assertEquals(user, submissionViewModel.student.value)
                assertEquals(homework, submissionViewModel.homework.value)
            }
        }
    }
})
