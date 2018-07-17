package org.schulcloud.mobile.models.user

import io.realm.Realm
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.storages.UserStorage

object UserRepository {
    val TAG: String = UserRepository::class.java.simpleName

    @JvmStatic
    val token: String?
        get() = UserStorage().accessToken

    @JvmStatic
    val userId: String?
        get() = UserStorage().userId

    @JvmStatic
    val isAuthorized: Boolean
        get() = UserStorage().accessToken != null

    fun login(email: String, password: String, callback: RequestJobCallback) {
        launch {
            // Login
            async {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }.await()

            // Sync data
            async {
                CourseRepository.syncCourses()
                NotificationRepository.syncDevices()
            }.await()
        }
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}
