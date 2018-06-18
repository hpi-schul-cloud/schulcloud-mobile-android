package org.schulcloud.mobile.utils

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmResults
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.base.RealmObjectLiveData
import org.schulcloud.mobile.models.content.ContentDao
import org.schulcloud.mobile.models.course.CourseDao
import org.schulcloud.mobile.models.topic.TopicDao


// Convenience extension on RealmResults to return as LiveRealmData
fun <T : RealmModel> RealmResults<T>.asLiveData(): LiveRealmData<T> = LiveRealmData(this)

// Convenience extension on RealmObject to return as RealmObjectLiveData
fun <T : RealmObject> T.asLiveData(): RealmObjectLiveData<T> = RealmObjectLiveData(this)

fun Realm.courseDao() : CourseDao = CourseDao(this)
fun Realm.topicDao() : TopicDao = TopicDao(this)
fun Realm.contentDao() : ContentDao = ContentDao(this)
