package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.switchMapNullable

class SubmissionViewModel(val id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val submission: LiveData<Submission?> = SubmissionRepository.submission(realm, id)
    val student: LiveData<User?> = submission
            .switchMapNullable {
                it?.studentId?.let { UserRepository.user(realm, it) }
            }

    val homework: LiveData<Homework?> = submission
            .switchMapNullable {
        it?.homeworkId?.let { HomeworkRepository.homework(realm, it) }
    }
}
