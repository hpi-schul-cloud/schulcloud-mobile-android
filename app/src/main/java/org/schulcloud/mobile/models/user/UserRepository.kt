package org.schulcloud.mobile.models.user
import io.realm.Realm
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.schulcloud.mobile.jobs.CreateAccessTokenJob
import org.schulcloud.mobile.jobs.GetUserJob
import org.schulcloud.mobile.jobs.PatchUserJob
import org.schulcloud.mobile.jobs.*
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.notifications.NotificationRepository
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.asLiveData

object UserRepository {
    val TAG: String = UserRepository::class.java.simpleName

    @JvmStatic
    val token: String?
        get() = UserStorage().accessToken

    @JvmStatic
    val isAuthorized: Boolean
        get() = UserStorage().accessToken != null


    fun login(email: String, password: String, callback: RequestJobCallback) {
        launch {
            // Login
            withContext(DefaultDispatcher) {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }
        }
    }

    fun currentUser(realm: Realm): RealmObjectLiveData<User>{
        return realm.where(User::class.java)
                .findFirstAsync()
                .asLiveData()
    }

    fun getAccount(realm: Realm): RealmObjectLiveData<Account>{
        return realm.where(Account::class.java)
                .findFirstAsync()
                .asLiveData()
    }

    suspend fun syncUser(userId: String){
        GetUserJob(userId, object : RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    suspend fun patchAccount(account: Account){
        PatchAccountJob(account,object: RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    suspend fun getAccountForUser(userId: String){
        GetAccountForUserJob(userId, object: RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    suspend fun patchUser(user: User){
       PatchUserJob(user, object : RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}
