package org.schulcloud.mobile.controllers.user_settings

import android.os.Bundle
import android.os.PersistableBundle
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.user.User

class UserSettingsActivity: BaseActivity(){
    private lateinit var mUser: User
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_user_settings)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mUser = outState!!["user"] as User

    }
}