package org.schulcloud.mobile.commonTest.rules

import org.schulcloud.mobile.controllers.main.MainActivity
import androidx.test.espresso.intent.rule.IntentsTestRule
import io.mockk.*
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.event.EventRepository
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.HomeworkRepository
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.news.NewsRepository
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.storages.Onboarding
import org.schulcloud.mobile.utils.asLiveData

class MainActivityIntentTestRule : IntentsTestRule<MainActivity>(MainActivity::class.java) {
    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()

        mockkStatic(UserRepository::class)
        every { UserRepository.isAuthorized } returns true
        mockkObject(Onboarding)
        // To avoid the TapTargetView of the NavigationDrawer
        every { Onboarding.navigation } returns mockk {
            every { getUpdates() } returns null
        }

        // mock repositories
        mockkObject(NewsRepository, EventRepository, HomeworkRepository)
        every { EventRepository.eventsForToday(any()) } returns emptyList<Event>().asLiveData()
        every { HomeworkRepository.openHomeworkForNextWeek(any()) } returns emptyList<Homework>().asLiveData()
        every { NewsRepository.newsList(any()) } returns emptyList<News>().asLiveData()
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()

        clearAllMocks()
    }
}

