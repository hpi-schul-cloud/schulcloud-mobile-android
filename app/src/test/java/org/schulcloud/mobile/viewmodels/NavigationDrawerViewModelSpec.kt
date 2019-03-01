package org.schulcloud.mobile.viewmodels

import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.user
import org.schulcloud.mobile.utils.asLiveData
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object NavigationDrawerViewModelSpec : Spek({
    val user = user("id")

    describe("A navigationDrawerViewModel") {
        val navigationDrawerViewModel by memoized {
            NavigationDrawerViewModel()
        }
        val mockRealm = mockk<Realm>()
        mockkObject(UserRepository)
        mockkStatic(Realm::class)

        beforeEach {
            prepareTaskExecutor()
            every { UserRepository.currentUser(mockRealm) } returns user.asLiveData()
            every { Realm.getDefaultInstance() } returns mockRealm
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            it("should return the correct data") {
                assertEquals(user, navigationDrawerViewModel.user.value)
            }
        }
    }
})
