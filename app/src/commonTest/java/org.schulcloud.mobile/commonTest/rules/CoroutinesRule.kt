package org.schulcloud.mobile.commonTest.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CoroutinesRule(private val dispatcher: ExecutorCoroutineDispatcher) : TestRule {
      override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                Dispatchers.setMain(dispatcher)

                base.evaluate()

                Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
                dispatcher.close()
            }
        }
    }
}
