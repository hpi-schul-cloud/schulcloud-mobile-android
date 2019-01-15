package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private const val COMPONENT = "component"
private const val TITLE = "title"
private const val HIDDEN = false
private val CONTENT = Content()

object ContentWrapperSpec : Spek({
    describe("A contentWrapper") {
        val contentWrapper by memoized {
            ContentWrapper().apply {
                component = COMPONENT
                title = TITLE
                hidden = HIDDEN
                content = CONTENT
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(COMPONENT, contentWrapper.component)
                assertEquals(TITLE, contentWrapper.title)
                assertEquals(HIDDEN, contentWrapper.hidden)
                assertEquals(CONTENT, contentWrapper.content)
            }
        }
    }
})
