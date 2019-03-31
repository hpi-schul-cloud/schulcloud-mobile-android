package org.schulcloud.mobile.controllers.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.NavigationViewActions.navigateTo
import androidx.test.espresso.matcher.ViewMatchers.*
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.schulcloud.mobile.R
import org.schulcloud.mobile.commonTest.checkActivityStarted
import org.schulcloud.mobile.commonTest.createAndAddIntentBlockingActivityMonitor
import org.schulcloud.mobile.commonTest.user
import org.schulcloud.mobile.controllers.login.LoginActivity
import org.schulcloud.mobile.controllers.settings.SettingsActivity
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.utils.asLiveData
import org.schulcloud.mobile.viewmodels.NavigationDrawerViewModel

open class NavigationDrawerFragmentTest {
    protected val userId = "userId"
    protected val firstName = "firstName"
    protected val lastName = "lastName"
    protected val user = user(userId).also {
        it.firstName = firstName
        it.lastName = lastName
    }
    protected val mockNavigationDrawerViewModel = mockk<NavigationDrawerViewModel>()
    protected val mockNavController = mockk<NavController>(relaxed = true)
    protected lateinit var navigationDrawerFragmentScenario: FragmentScenario<NavigationDrawerFragment>

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockkStatic(ViewModelProviders::class)
        every { ViewModelProviders.of(ofType<NavigationDrawerFragment>()) } returns mockk {
            every { get(NavigationDrawerViewModel::class.java) } returns mockNavigationDrawerViewModel
        }
        every { mockNavigationDrawerViewModel.user } returns user.asLiveData()

        navigationDrawerFragmentScenario = launchFragmentInContainer {
            NavigationDrawerFragment().also { fragment ->
                // to insert the mockNavController early enough in the fragment's lifecycle
                // https://developer.android.google.cn/guide/navigation/navigation-testing
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), mockNavController)
                    }
                }
            }
        }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    class NavigationMenuTest : NavigationDrawerFragmentTest() {
        @Test
        fun shouldDisplayUserName() {
            onView(withText("$firstName $lastName")).check(matches(isDisplayed()))
        }

        @Test
        fun shouldOpenSettingsActivityWhenOpenSettingsClicked() {
            val activityMonitor = createAndAddIntentBlockingActivityMonitor(SettingsActivity::class.java.name)
            onView(withId(R.id.openSettings)).perform(click())

            assertTrue(checkActivityStarted(activityMonitor))
        }

        @Test
        fun shouldCallLogoutAndOpenLoginActivityWhenLogoutClicked() {
            mockkObject(UserRepository)
            val activityMonitor = createAndAddIntentBlockingActivityMonitor(LoginActivity::class.java.name)
            onView(withId(R.id.logout)).perform(click())

            verify { UserRepository.logout() }
            assertTrue(checkActivityStarted(activityMonitor))
        }
    }

    @RunWith(Parameterized::class)
    class NavigationActionsTest(private val fragmentId: Int) : NavigationDrawerFragmentTest() {
        companion object {
            @JvmStatic
            @Parameterized.Parameters()
            fun data(): Collection<Array<Int>> = listOf(
                    arrayOf(R.id.fragment_dashboard),
                    arrayOf(R.id.fragment_courseList),
                    arrayOf(R.id.fragment_newsList),
                    arrayOf(R.id.fragment_homeworkList)

                    // Uncomment those when respective fragments are enabled/added
                   // arrayOf(R.id.fragment_calendar),
                   // arrayOf(R.id.fragment_fileOverview),
                   // arrayOf(R.id.fragment_learnstore)
            )
        }

        @Test
        fun shouldNavigateCorrectlyWhenMenuItemClicked() {
            onView(withId(R.id.navigationView)).perform(navigateTo(fragmentId))
            verify{ mockNavController.navigate(fragmentId, any(), any()) }
        }
    }
}
