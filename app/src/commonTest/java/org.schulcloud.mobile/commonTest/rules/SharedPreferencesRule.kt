package org.schulcloud.mobile.commonTest.rules

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockkObject
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.schulcloud.mobile.SchulCloudApp

class SharedPreferencesRule(private val name: String, private val sharedPreferences: SharedPreferences) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                mockkObject(SchulCloudApp)
                every { SchulCloudApp.instance.getSharedPreferences(name, Context.MODE_PRIVATE) } returns sharedPreferences

                base.evaluate()
            }
        }
    }
}
