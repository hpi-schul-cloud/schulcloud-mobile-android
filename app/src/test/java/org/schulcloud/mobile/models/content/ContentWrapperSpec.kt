package org.schulcloud.mobile.models.content

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object ContentWrapperSpec : Spek({
    val component = "component"
    val title = "title"
    val hidden = false
    val content = Content()

    describe("A contentWrapper") {
        val contentWrapper by memoized {
            ContentWrapper().apply {
                this.component = component
                this.title = title
                this.hidden = hidden
                this.content = content
            }
    }

        describe("property access") {
            it("should return the assigned value") {
                assertEquals(component, contentWrapper.component)
                assertEquals(title, contentWrapper.title)
                assertEquals(hidden, contentWrapper.hidden)
                assertEquals(content, contentWrapper.content)
            }
        }
    }
})
