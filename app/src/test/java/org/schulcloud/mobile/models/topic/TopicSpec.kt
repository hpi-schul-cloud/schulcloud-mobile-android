package org.schulcloud.mobile.models.topic

import io.realm.RealmList
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.utils.HOST
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val ID = "id"
private const val COURSEID = "courseId"
private const val NAME = "name"
private const val TIME = "time"
private const val POSITION = 1
private const val DATE = "date"
private val CONTENTS = RealmList<ContentWrapper>()
private val URL = "$HOST/courses/courseId/topics/id"

object TopicSpec : Spek({
    describe("A topic") {
        val topic by memoized {
            Topic().apply {
                id = ID
                courseId = COURSEID
                name = NAME
                time = TIME
                date = DATE
                position = POSITION
                contents = CONTENTS
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(ID, topic.id)
                assertEquals(COURSEID, topic.courseId)
                assertEquals(NAME, topic.name)
                assertEquals(TIME, topic.time)
                assertEquals(DATE, topic.date)
                assertEquals(POSITION, topic.position)
                assertEquals(CONTENTS, topic.contents)
            }

            it("url should include id and courseId"){
                assertEquals(URL, topic.url)
            }
        }
    }
})
