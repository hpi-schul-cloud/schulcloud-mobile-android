package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository

class AddAttachmentViewModel(id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val submission: LiveData<Submission?> = SubmissionRepository.submission(realm, id)

    suspend fun updateSubmission(value: Submission) {
        SubmissionRepository.updateSubmission(realm, value)
    }
}
