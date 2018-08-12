package org.schulcloud.mobile.controllers.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_user_settings.*
import kotlinx.coroutines.experimental.async
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.viewmodels.UserSettingsViewModel

class UserSettingsActivity: BaseActivity(){
    companion object {
        val TAG = UserSettingsActivity::class.java.simpleName
        const val EXTRA_ID = "org.schulcloud.extras.EXTRA_ID"
        fun newIntent(context: Context, id: String): Intent {
            return Intent(context, UserSettingsActivity::class.java)
                    .apply { putExtra(EXTRA_ID, id) }
        }
    }

    private lateinit var viewModel: UserSettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserSettingsViewModel::class.java)
        setContentView(R.layout.activity_user_settings)
        user_edit_submit.setOnClickListener({ patchUser() })

        viewModel.user.observe(this, Observer { user ->
            user_edit_email.setText(user!!.email)
            user_edit_forename.setText(user!!.firstName)
            user_edit_lastname.setText(user!!.lastName)
            user_edit_gender.setSelection(resources.getStringArray(R.array.genders).indexOf(user!!.gender))
        })

        viewModel.account.observe(this, Observer { account ->

        })
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
        user.firstName = user_edit_forename.text.toString()
        user.lastName = user_edit_lastname.text.toString()
        user.gender = user_edit_gender.selectedItem.toString()
        user.email = user_edit_email.text.toString()

        if(!cutSpaces(user_edit_new_password.text.toString()).equals("")){
            account.id = ""
            account.newPassword = user_edit_new_password.text.toString()
            account.newPasswordRepeat = user_edit_new_password_repeat.text.toString()

            async { viewModel.patchAccount(account) }
        }

        async { viewModel.patchUser(user) }
    }

    fun cutSpaces(input: String): String{
        return input.replace(" ","")
    }
}