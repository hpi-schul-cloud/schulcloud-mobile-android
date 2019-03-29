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
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudTestApp
import org.schulcloud.mobile.controllers.main.MainActivity
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.viewmodels.LoginViewModel

@RunWith(AndroidJUnit4::class)
@Config(application = SchulCloudTestApp::class)
class LoginFragmentTest {
    private val args = Bundle()
    private val email = "email"
    private val password = "password"
    private val appContext = ApplicationProvider.getApplicationContext<SchulCloudTestApp>()
    private val loginViewModel = spyk<LoginViewModel>()
    private val errorTextIdsForErrorCodes = mapOf(RequestJobCallback.ErrorCode.TIMEOUT to R.string.login_error_server,
            RequestJobCallback.ErrorCode.ERROR to R.string.login_error_passwordWrong,
            RequestJobCallback.ErrorCode.NO_NETWORK to R.string.login_error_noConnection,
            RequestJobCallback.ErrorCode.CANCEL to R.string.login_error,
            RequestJobCallback.ErrorCode.NO_AUTH to R.string.login_error,
            RequestJobCallback.ErrorCode.MAINTENANCE to R.string.login_error,
            RequestJobCallback.ErrorCode.API_VERSION_EXPIRED to R.string.login_error)
    private lateinit var loginFragmentScenario: FragmentScenario<LoginFragment>

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
        performLoginAction()

        onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
        assertEquals(shadowOf(appContext).peekNextStartedActivity().component?.className, MainActivity::class.java.canonicalName)
    }

    @Test
    fun shouldShowInvalidEmailErrorWhenLoginClickedWithInvalidEmail() {
        every { loginViewModel.login(email, password) } answers
                {
                    loginViewModel.loginState.value =
                            LoginViewModel.LoginStatus.InvalidInputs(mutableListOf(LoginViewModel.LoginInput.EMAIL))
                }
        performLoginAction()

        onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loginBtn)).check(matches(isEnabled()))
        onView(withId(R.id.emailInput)).check(matches(hasErrorText(appContext.getString(R.string.login_error_emailInvalid))))
    }

    @Test
    fun shouldShowInvalidPasswordErrorWhenLoginClickedWithInvalidPassword() {
        every { loginViewModel.login(email, password) } answers
                {
                    loginViewModel.loginState.value =
                            LoginViewModel.LoginStatus.InvalidInputs(mutableListOf(LoginViewModel.LoginInput.PASSWORD))
                }
        // input order is reverted here to set the focus on the emailInput field before login
        // because for some reason calling setError on a focused TextInputLayout leads to NullPointerException here
        onView(withId(R.id.passwordInput)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.emailInput)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.loginBtn)).perform(scrollTo(), click())

        onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
        onView(withId(R.id.loginBtn)).check(matches(isEnabled()))
        onView(withId(R.id.passwordInput)).check(matches(hasErrorText(appContext.getString(R.string.login_error_passwordEmpty))))
    }

    @Test
    fun shouldShowErrorTextWhenLoginClickedAndError() {
        errorTextIdsForErrorCodes.forEach { errorCode, errorTextId ->
            every { loginViewModel.login(email, password) } answers
                    { loginViewModel.loginState.value = LoginViewModel.LoginStatus.Error(errorCode) }
            performLoginAction()

            onView(withId(R.id.errorText)).check(matches(withText(appContext.getString(errorTextId))))
                    .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
            onView(withId(R.id.loginBtn)).check(matches(isEnabled()))
        }
    }

    private fun performLoginAction() {
        insertLoginInput(email, password)
        onView(withId(R.id.loginBtn)).perform(scrollTo(), click())
    }

    private fun insertLoginInput(email: String, password: String) {
        onView(withId(R.id.emailInput)).perform(clearText(), typeText(email), closeSoftKeyboard())
        onView(withId(R.id.passwordInput)).perform(clearText(), typeText(password), closeSoftKeyboard())
    }
}
