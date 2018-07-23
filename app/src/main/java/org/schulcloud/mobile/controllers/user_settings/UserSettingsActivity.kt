package org.schulcloud.mobile.controllers.user_settings

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_user_settings.*
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.viewmodels.UserSettingsViewModel

class UserSettingsActivity: BaseActivity(){

    private lateinit var userSettingsViewModel: UserSettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_user_settings)
        userSettingsViewModel = ViewModelProviders.of(this).get(UserSettingsViewModel::class.java)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View {
        user_edit_submit.setOnClickListener({l -> patchUser()})
        return super.onCreateView(name, context, attrs)
    }

    fun patchUser(){
        if(cutSpaces(user_edit_password.text.toString()).equals("")){
            user_edit_password.startAnimation(resources.getAnimation(R.anim.shake) as Animation)
            return
        }
        if(!cutSpaces(user_edit_new_password.text.toString()).equals("") && !user_edit_new_password.text.equals(user_edit_new_password_repeat)){
            user_edit_new_password.startAnimation(resources.getAnimation(R.anim.shake) as Animation)
            return
        }
        val user = User()
        val account = Account()
        user.firstName = user_edit_forename.text as String
        user.lastName = user_edit_lastname.text as String
        user.gender = user_edit_gender.selectedItem.toString()
        user.email = user_edit_email.text as String
        if(!cutSpaces(user_edit_new_password.text.toString()).equals("")){
            account.newPassword = user_edit_new_password.text as String
            account.newPasswordRepeat = user_edit_new_password_repeat.text as String
            async { userSettingsViewModel.patchAccount(account) }
        }
        async { userSettingsViewModel.patchUser(user) }
    }

    fun cutSpaces(input: String): String{
        return input.replace(" ","")
    }
}