package org.schulcloud.mobile.models.topic

import io.realm.RealmList
import org.schulcloud.mobile.models.content.ContentWrapper
import org.schulcloud.mobile.utils.HOST
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object TopicSpec : Spek({
    val id = "id"
    val courseId = "courseId"
    val name = "name"
    val time = "time"
    val position = 1
    val date = "date"
    val contents = RealmList<ContentWrapper>()
    val url = "$HOST/courses/courseId/topics/id"

    describe("A topic") {
        val topic by memoized {
            Topic().apply {
                this.id = id
                this.courseId = courseId
                this.name = name
                this.time = time
                this.date = date
                this.position = position
                this.contents = contents
            }
        }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(id, topic.id)
                assertEquals(courseId, topic.courseId)
                assertEquals(name, topic.name)
                assertEquals(time, topic.time)
                assertEquals(date, topic.date)
                assertEquals(position, topic.position)
                assertEquals(contents, topic.contents)
            }

            it("url should include id and courseId"){
                assertEquals(url, topic.url)
            }
        }
    }
})
