package org.schulcloud.mobile.controllers.login

import android.app.Instrumentation
import android.content.Intent
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.any
import org.junit.*
import org.junit.Assert.*
import org.schulcloud.mobile.R
import org.schulcloud.mobile.commonTest.EspressoIdlingResource
import org.schulcloud.mobile.commonTest.accessTokenJson
import org.schulcloud.mobile.commonTest.checkActivityStarted
import org.schulcloud.mobile.commonTest.createAndAddIntentBlockingActivityMonitor
import org.schulcloud.mobile.controllers.main.MainActivity
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.storages.UserStorage
import org.schulcloud.mobile.utils.API_URL
import org.schulcloud.mobile.utils.JwtUtil

class LoginProcessTest {
    private val email = "email@user.com"
    private val password = "password"
    private val userId = "userId"
    private val accessToken = "here.comes.anAccessToken"
    private val accessTokenJson = accessTokenJson()
    private val response = MockResponse().setResponseCode(200).setBody(accessTokenJson)
    private val mockServer = MockWebServer()
    private lateinit var url: String
    private val dispatcher = object : Dispatcher() {

        override fun dispatch(request: RecordedRequest): MockResponse {
            return if (request.path.equals("/authentication")) {
                response
            } else MockResponse()
        }
    }

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
        mockServer.start()
        url = mockServer.url("/").toString()

        mockRepositories()
        mockkObject(ApiService)
        mockkObject(JwtUtil)
        mockkStatic("org.schulcloud.mobile.utils.WebUtilsKt")
        every { API_URL } returns url
        every { JwtUtil.decodeToCurrentUser(accessToken) } returns userId
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun shouldStoreUserDataOnLogin() {
        mockServer.setDispatcher(dispatcher)

        onView(withId(R.id.emailInput)).perform(ViewActions.clearText(), ViewActions.typeText(email), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(ViewActions.clearText(), ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.loginBtn)).perform(ViewActions.scrollTo(), ViewActions.click())

        assertEquals(userId, UserStorage.userId)
        assertEquals(accessToken, UserStorage.accessToken)
    }

    private fun mockRepositories() {
        mockkObject(UserRepository, EventRepository, HomeworkRepository, CourseRepository, NewsRepository)
        coEvery { UserRepository.syncCurrentUser() } just runs
        coEvery { EventRepository.syncEvents() } just runs
        coEvery { HomeworkRepository.syncHomeworkList() } just runs
        coEvery { CourseRepository.syncCourses() } just runs
        coEvery { NewsRepository.syncNews() } just runs
    }
}

