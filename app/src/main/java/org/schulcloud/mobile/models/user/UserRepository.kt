package org.schulcloud.mobile.models.user

import io.realm.Realm
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
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
    val firstname: String?
        get() = UserStorage().firstname

    @JvmStatic
    val lastname: String?
        get() = UserStorage().lastname

    @JvmStatic
    val email: String?
        get() = UserStorage().email

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
            NewsRepository.syncNews()
            CourseRepository.syncCourses()
            EventRepository.syncEvents()
            HomeworkRepository.syncHomeworkList()
        }
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}
