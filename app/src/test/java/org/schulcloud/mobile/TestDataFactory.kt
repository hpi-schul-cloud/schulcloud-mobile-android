package org.schulcloud.mobile

import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.user.User

fun courseList(number: Int): List<Course> {
    val courses = mutableListOf<Course>()
    for (i in 1..number)
        courses.add(course(i.toString()))
    return courses
}

fun course(uniqueSequence : String): Course {
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

fun event(uniqueSequence : String): Event {
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

fun homework(uniqueSequence : String): Homework {
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

fun news(uniqueSequence : String): News {
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

fun user(uniqueSequence : String): User {
    return User().apply {
        id = uniqueSequence
    }
}
