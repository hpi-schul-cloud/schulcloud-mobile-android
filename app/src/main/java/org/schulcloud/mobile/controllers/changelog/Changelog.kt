package org.schulcloud.mobile.controllers.changelog

import com.michaelflisar.changelog.ChangelogBuilder
import com.michaelflisar.changelog.ChangelogSetup
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.utils.DarkModeUtils


object Changelog {
    fun configure() {
        ChangelogSetup.get().registerTag(IntroTag())
        ChangelogSetup.get().registerTag(FeatureTag())
        ChangelogSetup.get().registerTag(FixTag())
        ChangelogSetup.get().registerTag(NoteTag())
    }

    fun showRecentsDialog(activity: BaseActivity) {
        ChangelogBuilder().apply {
            withMinVersionToShow(BuildConfig.VERSION_CODE)
            withManagedShowOnStart(true)
            withRenderer(Renderer())
        }.buildAndShowDialog(activity, DarkModeUtils.getInstance(activity).isActive)
    }
}
