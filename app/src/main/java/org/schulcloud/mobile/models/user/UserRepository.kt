package org.schulcloud.mobile.models.user
import android.arch.lifecycle.LiveData
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
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.utils.userDao

object UserRepository {
    val TAG: String = UserRepository::class.java.simpleName
    var patchStage = 0

    @JvmStatic
    val token: String?
        get() = UserStorage().accessToken

    @JvmStatic
    val isAuthorized: Boolean
        get() = UserStorage().accessToken != null

    @JvmStatic
    val userId: String?
        get() = UserStorage().userId


    fun login(email: String, password: String, callback: RequestJobCallback) {
        launch {
            // Login
            withContext(DefaultDispatcher) {
                CreateAccessTokenJob(Credentials(email, password), callback).run()
            }
        }
    }

    fun currentUser(realm: Realm): LiveData<User?> {
        return realm.userDao().currentUser()
    }

    fun getAccount(realm: Realm): LiveData<Account?>{
        return realm.userDao().getAccount()
    }

    suspend fun syncUser(userId: String){
        GetUserJob(userId, object : RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    suspend fun patchAccount(account: Account,callback: RequestJobCallback){
        PatchAccountJob(account,callback)
        patchStage = 0
    }

    suspend fun getAccountForUser(userId: String){
        GetAccountForUserJob(userId, object: RequestJobCallback(){
            override fun onSuccess() {}
            override fun onError(code: RequestJobCallback.ErrorCode) {}
        }).run()
    }

    suspend fun patchUser(user: User,callback: RequestJobCallback){
       PatchUserJob(user,callback).run()
    }

    fun logout() {
        UserStorage().delete()

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { it.deleteAll() }
        realm.close()
    }
}
