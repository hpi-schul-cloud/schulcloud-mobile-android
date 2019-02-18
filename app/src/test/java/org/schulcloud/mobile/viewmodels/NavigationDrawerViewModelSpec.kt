package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.mockRealmDefaultInstance
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.user
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

private val user = user("id")
private lateinit var mockRealm: Realm

object NavigationDrawerViewModelSpec : Spek({
    describe("A navigationDrawerViewModel") {
        val navigationDrawerViewModel by memoized {
            NavigationDrawerViewModel()
        }

        beforeEachTest {
            prepareTaskExecutor()
            mockRealm = mockk()
            mockRealmDefaultInstance(mockRealm)

            mockkObject(UserRepository)
            every { UserRepository.currentUser(mockRealm) } returns user.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            unmockkObject(UserRepository)
            unmockkStatic(Realm::class)
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(user, navigationDrawerViewModel.user.value)
            }
        }
    }
})
