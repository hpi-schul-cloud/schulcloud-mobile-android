package org.schulcloud.mobile.models.topic

import io.realm.Realm
import org.schulcloud.mobile.jobs.ListCourseTopicsJob
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.utils.topicDao

/**
 * Date: 6/10/2018
 */
object TopicRepository {


    fun listTopics(realm: Realm, courseId: String): LiveRealmData<Topic> {
        requestTopics(courseId)
        return realm.topicDao().listTopics(courseId)
    }

    fun topic(realm: Realm, id: String): RealmObjectLiveData<Topic> {
        return realm.topicDao().topic(id)
    }

    private fun requestTopics(courseId: String) {
        ListCourseTopicsJob(courseId, object : RequestJobCallback() {
            override fun onSuccess() {
            }

            override fun onError(code: ErrorCode) {
            }
        }).run()
    }
}
