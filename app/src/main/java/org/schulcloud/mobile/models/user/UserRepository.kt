package org.schulcloud.mobile.models.user

import io.realm.Realm
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.news.NewsRepository
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
            withContext(DefaultDispatcher) {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }

            // Sync data in background
            CourseRepository.syncCourses()
            NewsRepository.syncNews()
        }
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}
