package org.schulcloud.mobile.viewmodels

import io.mockk.*
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.prepareTaskExecutor
import org.schulcloud.mobile.resetTaskExecutor
import org.schulcloud.mobile.utils.emailIsValid
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object LoginViewModelSpec : Spek({
    val validEmail = "email@domain.web"
    val invalidEmail = "email"
    val validPassword = "password"
    val invalidPassword = ""
    val callbackSlot = slot<RequestJobCallback>()
    val loginInputCases =
            mapOf(invalidEmail to Pair(false,
                                        mapOf(invalidPassword to Triple(false,
                                                                        "invalid in email and password",
                                                                        LoginViewModel.LoginStatus.InvalidInputs(
                                                                                mutableListOf(
                                                                                    LoginViewModel.LoginInput.EMAIL,
                                                                                    LoginViewModel.LoginInput.PASSWORD))),
                                                validPassword to Triple(true,
                                                                        "invalid in email",
                                                                        LoginViewModel.LoginStatus.InvalidInputs(
                                                                                mutableListOf(
                                                                                    LoginViewModel.LoginInput.EMAIL))))),
                    validEmail to Pair(true,
                                        mapOf(invalidPassword to Triple(false,
                                                                        "invalid in password",
                                                                        LoginViewModel.LoginStatus.InvalidInputs(
                                                                                mutableListOf(
                                                                                    LoginViewModel.LoginInput.PASSWORD))),
                                                validPassword to Triple(true,
                                                                        "pending",
                                                                        LoginViewModel.LoginStatus.Pending()))))

    describe("A loginViewModel") {
        val loginViewModel by memoized {
            LoginViewModel()
        }
        mockkObject(UserRepository)
        mockkStatic("org.schulcloud.mobile.utils.AndroidUtilsKt")

        beforeEach {
            prepareTaskExecutor()
            every { emailIsValid(validEmail) } returns true
            every { emailIsValid(invalidEmail) } returns false
        }

        afterEach {
            resetTaskExecutor()
            clearAllMocks()
        }

        describe("inserting login input") {
            loginInputCases.forEach {email, isValidAndPasswordCases ->
                val emailIsValid = isValidAndPasswordCases.first
                val passwordCases = isValidAndPasswordCases.second

                describe("inserting ${if(emailIsValid) "" else "in"}valid email"){
                    passwordCases.forEach { password, isValidAndLoginStateTextAndLoginStatus ->
                        val passwordIsValid = isValidAndLoginStateTextAndLoginStatus.first
                        val expectedLoginStateText = isValidAndLoginStateTextAndLoginStatus.second
                        val expectedLoginState = isValidAndLoginStateTextAndLoginStatus.third

                        describe("inserting ${if(passwordIsValid) "" else "in"}valid password"){
                            beforeEach {
                                loginViewModel.login(email, password)
                            }

                            it("loginState should be $expectedLoginStateText"){
                                assertEquals(expectedLoginState::class, loginViewModel.loginState.value!!::class)

                                if(loginViewModel.loginState.value is LoginViewModel.LoginStatus.InvalidInputs
                                        && expectedLoginState is LoginViewModel.LoginStatus.InvalidInputs){
                                    val loginState = loginViewModel.loginState.value as LoginViewModel.LoginStatus.InvalidInputs
                                    assertEquals(expectedLoginState.invalidInputs, loginState.invalidInputs)
                                }
                            }
                        }
                    }
                }
            }
        }

        describe("login with valid inputs"){
            describe("positive response from repository"){
                beforeEach {
                    every { UserRepository.login(validEmail,validPassword, callback = capture(callbackSlot)) } answers{
                        callbackSlot.captured.success()
                    }
                    loginViewModel.login(validEmail, validPassword)
                }

                it("loginState should be logged in"){
                    assert(loginViewModel.loginState.value is LoginViewModel.LoginStatus.LoggedIn)
                }
            }

            describe("negative response from repository"){
                beforeEach {
                    every { UserRepository.login(validEmail,validPassword, callback = capture(callbackSlot)) } answers{
                        callbackSlot.captured.error(RequestJobCallback.ErrorCode.ERROR)
                    }
                    loginViewModel.login(validEmail, validPassword)
                }

                it("loginState should be error"){
                    assert(loginViewModel.loginState.value is LoginViewModel.LoginStatus.Error)
                    val loginState = loginViewModel.loginState.value as LoginViewModel.LoginStatus.Error
                    assertEquals(RequestJobCallback.ErrorCode.ERROR.toString(), loginState.error)
                }
            }
        }
    }
})
