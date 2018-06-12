package org.schulcloud.mobile.utils

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults
import org.schulcloud.mobile.models.base.LiveRealmData
import org.schulcloud.mobile.models.course.CourseDao
import org.schulcloud.mobile.models.homework.HomeworkDao


// Convenience extension on RealmResults to return as LiveRealmData
fun <T : RealmModel> RealmResults<T>.asLiveData(): LiveRealmData<T> = LiveRealmData(this)

// DAOs
fun Realm.courseDao() : CourseDao = CourseDao(this)
fun Realm.homeworkDao() : HomeworkDao = HomeworkDao(this)