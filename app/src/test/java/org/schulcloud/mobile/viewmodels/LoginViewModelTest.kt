package org.schulcloud.mobile.viewmodels

import androidx.lifecycle.Observer
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.schulcloud.mobile.SchulCloudTestApp
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.emailIsValid

//@RunWith(RobolectricTestRunner::class)
//@Config(application = SchulCloudTestApp::class)
open class LoginViewModelTest {
    protected lateinit var loginViewModel: LoginViewModel
    protected val validEmail = "email@domain.web"
    protected val validPassword = "password"
    protected val callbackSlot = slot<RequestJobCallback>()
    protected val loginStates = mutableListOf<LoginViewModel.LoginStatus>()
    protected val observer = spyk<Observer<LoginViewModel.LoginStatus>>()

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel()
        mockkObject(UserRepository)
        mockkStatic("org.schulcloud.mobile.utils.AndroidUtilsKt")
        prepareTaskExecutor()
        every { emailIsValid(validEmail) } returns true
    }

    @After
    fun tearDown() {
        resetTaskExecutor()
        clearAllMocks()
    }

    @RunWith(ParameterizedRobolectricTestRunner::class)
    @Config(application = SchulCloudTestApp::class)
    class FailedLoginTest(private val errorCode: RequestJobCallback.ErrorCode) : LoginViewModelTest() {
        companion object {
            @JvmStatic
            @ParameterizedRobolectricTestRunner.Parameters(name = "ErrorCode = {0}")
            fun data(): Collection<Array<RequestJobCallback.ErrorCode>> = listOf(
                    arrayOf(RequestJobCallback.ErrorCode.ERROR),
                    arrayOf(RequestJobCallback.ErrorCode.CANCEL),
                    arrayOf(RequestJobCallback.ErrorCode.NO_NETWORK),
                    arrayOf(RequestJobCallback.ErrorCode.NO_AUTH),
                    arrayOf(RequestJobCallback.ErrorCode.MAINTENANCE),
                    arrayOf(RequestJobCallback.ErrorCode.API_VERSION_EXPIRED)
            )
        }

        @Test
        fun loginStateShouldBeErrorWithReceivedErrorCodeWhenLoginFails() {
            every { UserRepository.login(validEmail, validPassword, callback = capture(callbackSlot)) } answers {
                callbackSlot.captured.error(errorCode)
            }
            loginViewModel.loginState.observeForever(observer)
            loginViewModel.login(validEmail, validPassword)

            verify { observer.onChanged(capture(loginStates)) }
            val errorLoginStates = loginStates.filter { loginState -> loginState is LoginViewModel.LoginStatus.Error }
            assertTrue(errorLoginStates.isNotEmpty())
            errorLoginStates.forEach { errorLoginState ->
                assertEquals(errorCode.toString(), (errorLoginState as LoginViewModel.LoginStatus.Error).error)
            }
        }

        @Test
        fun loginStateShouldNeverBeLoggedInWhenLoginFails() {
            every { UserRepository.login(validEmail, validPassword, callback = capture(callbackSlot)) } answers {
                callbackSlot.captured.error(errorCode)
            }
            loginViewModel.loginState.observeForever(observer)
            loginViewModel.login(validEmail, validPassword)

            verify { observer.onChanged(capture(loginStates)) }
            val successfulLoginStates = loginStates.filter { loginState -> loginState is LoginViewModel.LoginStatus.LoggedIn }
            assertTrue(successfulLoginStates.isEmpty())
        }
    }

    @RunWith(RobolectricTestRunner::class)
    @Config(application = SchulCloudTestApp::class)
    class SuccessfulLoginTest : LoginViewModelTest() {

        @Test
        fun loginStateShouldBeLoggedInWhenLoginSucceeds() {
            every { UserRepository.login(validEmail, validPassword, callback = capture(callbackSlot)) } answers {
                callbackSlot.captured.success()
            }
            loginViewModel.loginState.observeForever(observer)
            loginViewModel.login(validEmail, validPassword)

            verify { observer.onChanged(ofType(LoginViewModel.LoginStatus.LoggedIn::class)) }
        }

        @Test
        fun loginStateShouldNeverBeErrorWhenLoginSucceeds() {
            every { UserRepository.login(validEmail, validPassword, callback = capture(callbackSlot)) } answers {
                callbackSlot.captured.success()
            }
            loginViewModel.loginState.observeForever(observer)
            loginViewModel.login(validEmail, validPassword)

            verify { observer.onChanged(capture(loginStates)) }
            val errorLoginStates = loginStates.filter { loginState -> loginState is LoginViewModel.LoginStatus.Error }
            assertTrue(errorLoginStates.isEmpty())
        }
    }
}
