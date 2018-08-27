package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.*

class HomeworkViewModel(val id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val homework: LiveData<Homework?> = HomeworkRepository.homework(realm, id)
    val course: LiveData<Course?> = homework.switchMapNullable {
        it?.course?.id?.let { CourseRepository.course(realm, it) }
    }
    val submissions: LiveData<List<Pair<User, Submission?>>> = SubmissionRepository.submissionsForHomework(realm, id)
            .combineLatest(course)
            .map { (submissions, course) ->
                if (course == null)
                    return@map emptyList<Pair<User, Submission>>()

                (course.users ?: emptyList<User>()).map { user ->
                    user to submissions.firstOrNull { it.studentId == user.id }
                }
            }

    var selectionByUser = false
    val selectedStudent: MutableLiveData<User?> = homework
            .filter { it != null }
            .first()
            .switchMapNullable { homework ->
                return@switchMapNullable if (!homework!!.isTeacher(UserRepository.userId!!))
                    UserRepository.currentUser(realm)
                else null
            }
            .toMutableLiveData()
    val selectedSubmission: LiveData<Submission?> = homework
            .combineLatest(selectedStudent)
            .switchMapNullable { (homework, student) ->
                if (homework == null || student == null)
                    return@switchMapNullable null

                return@switchMapNullable SubmissionRepository.submission(realm, homework.id, student.id)
            }
}
