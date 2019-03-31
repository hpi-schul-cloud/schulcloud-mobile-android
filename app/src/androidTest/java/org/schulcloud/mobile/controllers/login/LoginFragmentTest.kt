package org.schulcloud.mobile.controllers.login

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.FragmentScenario.launchInContainer
import androidx.lifecycle.ViewModelProviders
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import io.mockk.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.commonTest.checkActivityStarted
import org.schulcloud.mobile.commonTest.createAndAddIntentBlockingActivityMonitor
import org.schulcloud.mobile.controllers.main.MainActivity
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.viewmodels.LoginViewModel

/**
 * These tests only run on devices or emulators with Android version 28+ due to missing static mocking support
 * for older versions by Mockk. See https://mockk.io/ANDROID
 * They may be moved to /test folder and be run with Robolectric if some Robolectric issues with TextView
 * sizes get fixed in future, currently some tests fail because of them.
 */
open class LoginFragmentTest {
    private val args = Bundle()
    protected val email = "email"
    protected val password = "password"
    protected val appContext = ApplicationProvider.getApplicationContext<SchulCloudApp>()
    protected val loginViewModel = spyk<LoginViewModel>()
    protected lateinit var loginFragmentScenario: FragmentScenario<LoginFragment>

    @Before
    fun setUp() {
        mockkStatic(ViewModelProviders::class)
        every { ViewModelProviders.of(ofType<LoginFragment>()) } returns mockk {
            every { get(LoginViewModel::class.java) } returns loginViewModel
        }
        every { loginViewModel.login(any(), any()) } just runs

        loginFragmentScenario = launchInContainer(LoginFragment::class.java, args, R.style.AppTheme, null)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    class LoginTest : LoginFragmentTest() {
        @Test
        fun shouldLoginWithDemoStudentAccountWhenDemoStudentClicked() {
            onView(withId(R.id.demo_student)).perform(scrollTo(), click())
            verify {
                loginViewModel.login(appContext.getString(R.string.login_demo_student_username),
                        appContext.getString(R.string.login_demo_student_password))
            }
        }

        @Test
        fun shouldLoginWithDemoTeacherAccountWhenDemoTeacherClicked() {
            onView(withId(R.id.demo_teacher)).perform(scrollTo(), click())
            verify {
                loginViewModel.login(appContext.getString(R.string.login_demo_teacher_username),
                        appContext.getString(R.string.login_demo_teacher_password))
            }
        }

        @Test
        fun shouldCallViewModelWithLoginInputWhenLoginClicked() {
            performLoginAction()
            verify { loginViewModel.login(email, password) }
        }

        @Test
        fun shouldShowProgressBarAndDisableLoginButtonWhenLoginClickedAndPending() {
            every { loginViewModel.login(email, password) } answers
                    { loginViewModel.loginState.value = LoginViewModel.LoginStatus.Pending }
            performLoginAction()

            onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
            onView(withId(R.id.progress)).check(matches(isDisplayed()))
            onView(withId(R.id.loginBtn)).check(matches(not(isEnabled())))
        }

        @Test
        fun shouldStartMainActivityWhenLoginClickedAndSuccessful() {
            every { loginViewModel.login(email, password) } answers
                    { loginViewModel.loginState.value = LoginViewModel.LoginStatus.LoggedIn }
            val activityMonitor = createAndAddIntentBlockingActivityMonitor(MainActivity::class.java.name)

            performLoginAction()

            assertTrue(checkActivityStarted(activityMonitor))
        }
    }

    @RunWith(Parameterized::class)
    class InvalidInputsTest(private val invalidInputsText: String,
                            private val invalidInputs: MutableList<LoginViewModel.LoginInput>,
                            private val inputFieldIdWithErrorTextId: List<Pair<Int, Int>>) : LoginFragmentTest() {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "Invalid {0}")
            fun data(): Collection<Array<Any>> = listOf(
                    arrayOf(LoginViewModel.LoginInput.EMAIL.toString(),
                            mutableListOf(LoginViewModel.LoginInput.EMAIL),
                            listOf(Pair(R.id.emailInput, R.string.login_error_emailInvalid))),
                    arrayOf(LoginViewModel.LoginInput.PASSWORD.toString(),
                            mutableListOf(LoginViewModel.LoginInput.PASSWORD),
                            listOf(Pair(R.id.passwordInput, R.string.login_error_passwordEmpty))),
                    arrayOf(LoginViewModel.LoginInput.EMAIL.toString() + "and "
                            + LoginViewModel.LoginInput.PASSWORD.toString(),
                            mutableListOf(LoginViewModel.LoginInput.EMAIL, LoginViewModel.LoginInput.PASSWORD),
                            listOf(Pair(R.id.emailInput, R.string.login_error_emailInvalid),
                                    Pair(R.id.passwordInput, R.string.login_error_passwordEmpty)))
            )
        }

        @Test
        fun shouldShowInvalidFieldsErrorWhenLoginClickedWithInvalidFields() {
            every { loginViewModel.login(email, password) } answers
                    {
                        loginViewModel.loginState.value =
                                LoginViewModel.LoginStatus.InvalidInputs(invalidInputs)
                    }
            performLoginAction()

            onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
            onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
            onView(withId(R.id.loginBtn)).check(matches(isEnabled()))

            inputFieldIdWithErrorTextId.forEach { idWithErrorText ->
                val inputFieldId = idWithErrorText.first
                val errorTextId = idWithErrorText.second

                onView(withId(inputFieldId)).check(matches(hasErrorText(appContext.getString(errorTextId))))
            }
        }
    }

    @RunWith(Parameterized::class)
    class ErrorLoginTest(private val errorCode: RequestJobCallback.ErrorCode,
                         private val errorTextId: Int) : LoginFragmentTest() {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "ErrorCode = {0}")
            fun data(): Collection<Array<Any>> = listOf(
                    arrayOf(RequestJobCallback.ErrorCode.TIMEOUT, R.string.login_error_server),
                    arrayOf(RequestJobCallback.ErrorCode.ERROR, R.string.login_error_passwordWrong),
                    arrayOf(RequestJobCallback.ErrorCode.NO_NETWORK, R.string.login_error_noConnection),
                    arrayOf(RequestJobCallback.ErrorCode.CANCEL, R.string.login_error),
                    arrayOf(RequestJobCallback.ErrorCode.NO_AUTH, R.string.login_error),
                    arrayOf(RequestJobCallback.ErrorCode.MAINTENANCE, R.string.login_error),
                    arrayOf(RequestJobCallback.ErrorCode.API_VERSION_EXPIRED, R.string.login_error)
            )
        }

        @Test
        fun shouldShowErrorTextWhenLoginClickedAndError() {
            every { loginViewModel.login(email, password) } answers
                    { loginViewModel.loginState.value = LoginViewModel.LoginStatus.Error(errorCode) }
            performLoginAction()

            onView(withId(R.id.errorText)).check(matches(withText(appContext.getString(errorTextId))))
                    .check(matches(isDisplayed()))
            onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
            onView(withId(R.id.loginBtn)).check(matches(isEnabled()))

        }
    }

    protected fun performLoginAction() {
        insertLoginInput(email, password)
        onView(withId(R.id.loginBtn)).perform(scrollTo(), click())
    }

    private fun insertLoginInput(email: String, password: String) {
        onView(withId(R.id.emailInput)).perform(clearText(), typeText(email), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(clearText(), typeText(password), closeSoftKeyboard())
    }
}
