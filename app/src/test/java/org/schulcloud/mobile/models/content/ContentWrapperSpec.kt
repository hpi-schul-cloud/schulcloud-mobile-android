package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val COMPONENT = "component"
private const val TITLE = "title"
private const val HIDDEN = false
private val content = Content()

object ContentWrapperSpec : Spek({
    describe("A contentWrapper") {
        val contentWrapper by memoized {
            ContentWrapper().also {
                it.component = COMPONENT
                it.title = TITLE
                it.hidden = HIDDEN
                it.content = content
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(COMPONENT, contentWrapper.component)
                assertEquals(TITLE, contentWrapper.title)
                assertEquals(HIDDEN, contentWrapper.hidden)
                assertEquals(content, contentWrapper.content)
            }
        }
    }
})
