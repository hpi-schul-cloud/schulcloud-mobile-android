package org.schulcloud.mobile.controllers.main

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.schulcloud.mobile.R
import org.schulcloud.mobile.SchulCloudApp
import org.schulcloud.mobile.commonTest.rules.MainActivityIntentTestRule

class MainActivityTest {
    private val appContext = ApplicationProvider.getApplicationContext<SchulCloudApp>()

    @Rule
    @JvmField
    val activityRule = MainActivityIntentTestRule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun shouldShowBottomAppBarOnDefault() {
        onView(withId(R.id.bottomAppBar)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldShareLinkWhenShareClicked() {
        val intent = Intent()
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(allOf(hasAction(equalTo(Intent.ACTION_CHOOSER)))).respondWith(result)

        onView(withId(R.id.base_action_share)).perform(click())

        intended(allOf(
                hasExtras(allOf(
                        hasEntry(equalTo(Intent.EXTRA_INTENT), hasAction(Intent.ACTION_SEND)),
                        hasEntry(equalTo(Intent.EXTRA_TITLE), containsString(appContext.getString(R.string.share_title))))),
                hasAction(equalTo(Intent.ACTION_CHOOSER))))
    }
}
