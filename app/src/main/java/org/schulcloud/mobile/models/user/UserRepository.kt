package org.schulcloud.mobile.models.user

import io.realm.Realm
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.storages.UserStorage

object UserRepository {

    val TAG: String = UserRepository::class.java.simpleName

    @JvmStatic
    val token: String?
        get() {
            return UserStorage().accessToken
        }

    @JvmStatic
    val userId: String?
        get() {
            return UserStorage().userId
        }

    @JvmStatic
    val isAuthorized: Boolean
        get() {
            return UserStorage().accessToken != null
        }

    fun login(email: String, password: String, callback: RequestJobCallback) {
        CreateAccessTokenJob(Credentials(email, password), callback).run()
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}