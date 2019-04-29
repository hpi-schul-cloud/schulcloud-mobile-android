package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.Observer
import io.mockk.*
import io.realm.Realm
import org.schulcloud.mobile.commonTest.mockRealmDefaultInstance
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.commonTest.prepareTaskExecutor
import org.schulcloud.mobile.commonTest.resetTaskExecutor
import org.schulcloud.mobile.commonTest.user
import org.schulcloud.mobile.models.user.User
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
        val observer = spyk<Observer<User>>()

        beforeGroup {
            mockRealmDefaultInstance(mockRealm)
            mockkObject(UserRepository)
        }

        beforeEach {
            prepareTaskExecutor()
            every { UserRepository.currentUser(mockRealm) } returns user.asLiveData()
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("data access") {
            beforeEach {
                navigationDrawerViewModel.user.observeForever(observer)
            }

            it("should return the correct data") {
                assertEquals(user, navigationDrawerViewModel.user.value)
            }
        }
    }
})
