package org.schulcloud.mobile.jobs

import android.util.Log
import io.realm.Realm
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.jobs.base.RequestJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.network.ApiService
import ru.gildor.coroutines.retrofit.awaitResponse

class ListUserNewsJob (callback: RequestJobCallback): RequestJob(callback) {

    companion object {
        val TAG: String = ListUserNewsJob::class.java.simpleName
    }

    override suspend fun onRun() {

       /* val response = ApiService.getInstance().listUserNews().awaitResponse();
        if (response.isSuccessful){

            if (BuildConfig.DEBUG)
                Log.i(TAG, "News recieved")

            //save news
            val recievedNews = response.body()!!.data!!

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction{
                for (news in recievedNews){
                    realm.copyToRealmOrUpdate(news)
                }

            }
            realm.close()
        }
        else {
            if (BuildConfig.DEBUG)
                Log.e(TAG, "Error while fetching news list")
            callback?.error(RequestJobCallback.ErrorCode.ERROR)
        }*/

        //creates Realm with dummy data

            var news1: News = News()
            news1.id = "1"; news1.title = "news 1"; news1.createdAt = "2018-03-01"; news1.content = "content of news 1"; news1.schoolId = "1"
            var news2: News = News()
            news2.id = "2"; news2.title = "news 2"; news2.createdAt = "2018-01-13"; news2.content = "content of news 2"; news2.schoolId = "2"
            var news3: News = News()
            news3.id = "3"; news3.title = "news 3"; news3.createdAt = "2018-03-02"; news3.schoolId = "3"
        var recievedNews: List<News> = listOf(news1, news2, news3)

        val realm = Realm.getDefaultInstance()
        realm.executeTransaction{
            for (news in recievedNews){
                realm.copyToRealmOrUpdate(news)
            }

        }
        realm.close()

    }
}