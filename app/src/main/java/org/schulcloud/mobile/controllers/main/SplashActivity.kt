package org.schulcloud.mobile.controllers.main

import android.os.Bundle
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.storages.UserStorage

class SplashActivity : AppCompatActivity() {

    companion object {
        val TAG: String = SplashActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        async{syncRepos()}

        if(UserRepository.isAuthorized) {
            startApp()
        } else {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    suspend private fun syncRepos(){
        NewsRepository.syncNews()
        CourseRepository.syncCourses()
        EventRepository.syncEvents()
        HomeworkRepository.syncHomeworkList()
        UserRepository.syncUser(UserStorage().userId!!)
        UserRepository.getAccountForUser(UserStorage().userId!!)
        NotificationRepository.syncDevices()
        CourseRepository.syncCourses()
    }

    private fun startApp() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
