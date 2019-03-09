package org.schulcloud.mobile

import io.realm.RealmModel
import io.realm.RealmObject
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.file.Directory
import org.schulcloud.mobile.models.file.File
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.user.User

fun courseList(number: Int): List<Course> {
    val courses = mutableListOf<Course>()
    for (i in 1..number)
        courses.add(course(i.toString()))
    return courses
}

fun course(uniqueSequence: String): Course {
    return Course().apply {
        id = uniqueSequence
    }
}

fun eventList(number: Int): List<Event> {
    val events = mutableListOf<Event>()
    for (i in 1..number)
        events.add(event(i.toString()))
    return events
}

fun event(uniqueSequence: String): Event {
    return Event().apply {
        id = uniqueSequence
    }
}

fun homeworkList(number: Int): List<Homework> {
    val homeworkList = mutableListOf<Homework>()
    for (i in 1..number)
        homeworkList.add(homework(i.toString()))
    return homeworkList
}

fun homework(uniqueSequence: String): Homework {
    return Homework().apply {
        id = uniqueSequence
    }
}

fun newsList(number: Int): List<News> {
    val newsList = mutableListOf<News>()
    for (i in 1..number)
        newsList.add(news(i.toString()))
    return newsList
}

fun news(uniqueSequence: String): News {
    return News().apply {
        id = uniqueSequence
    }
}

fun topicList(number: Int): List<Topic> {
    val topicList = mutableListOf<Topic>()
    for (i in 1..number)
        topicList.add(topic(i.toString()))
    return topicList
}

fun topic(uniqueSequence: String): Topic {
    return Topic().apply {
        id = uniqueSequence
    }
}

fun user(uniqueSequence: String): User {
    return User().apply {
        id = uniqueSequence
    }
}

fun userList(number: Int): List<User> {
    val userList = mutableListOf<User>()
    for (i in 1..number)
        userList.add(user(i.toString()))
    return userList
}

fun file(uniqueSequence: String): File {
    return File().apply {
        key = uniqueSequence
    }
}

fun fileList(number: Int): List<File> {
    val fileList = mutableListOf<File>()
    for (i in 1..number)
        fileList.add(file(i.toString()))
    return fileList
}

fun directory(uniqueSequence: String): Directory {
    return Directory().apply {
        key = uniqueSequence
    }
}

fun directoryList(number: Int): List<Directory> {
    val directoryList = mutableListOf<Directory>()
    for (i in 1..number)
        directoryList.add(directory(i.toString()))
    return directoryList
}

fun submission(uniqueSequence: String):Submission {
    return Submission().apply {
        id = uniqueSequence
    }
}

fun submissionWithStudent(submissionId: String, studentId: String):Submission {
    return submission(submissionId).apply {
        this.studentId = studentId
    }
}

fun submissionListWithStudents(number:Int) : List<Submission> {
    val submissionList = mutableListOf<Submission>()
    for (i in 1..number)
        submissionList.add(submissionWithStudent(i.toString(), i.toString()))
    return submissionList
}

fun realmModelList(number:Int) : List<RealmModel> {
    val realmModelList = mutableListOf<RealmModel>()
    for (i in 1..number)
        realmModelList.add(object : RealmObject(){})
    return realmModelList
}
