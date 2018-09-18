package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.file.FilePermissions
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.homework.submission.SubmissionRepository


class AddAttachmentViewModel(id: String) : ViewModel() {
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    val submission: LiveData<Submission?> = SubmissionRepository.submission(realm, id)

    suspend fun addFileToSubmission(submission: Submission, file: File) {
        submission.fileIds = (submission.fileIds ?: RealmList()).apply { add(file.id) }

        launch(UI) {
            SubmissionRepository.updateSubmission(realm, submission)

            val teacherId = submission.homeworkId
                    ?.let { HomeworkRepository.homeworkBlocking(realm, it) }?.teacherId
                    ?: return@launch

            file.addPermissions(listOf(teacherId),
                    listOf(FilePermissions.PERMISSION_READ, FilePermissions.PERMISSION_WRITE))
            FileRepository.updateFile(realm, file)
        }
    }
}
