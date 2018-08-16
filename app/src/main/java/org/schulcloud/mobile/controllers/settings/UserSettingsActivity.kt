package org.schulcloud.mobile.controllers.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
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

    private lateinit var mAccount: Account
    private lateinit var mUser: User
    private lateinit var viewModel: UserSettingsViewModel
    private lateinit var mShake: AnimationSet
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserSettingsViewModel::class.java)
        setContentView(R.layout.activity_user_settings)
        user_edit_submit.setOnClickListener({ async{ patch() } })

        viewModel.user.observe(this, Observer { user ->
            user_edit_email.setText(user!!.email)
            user_edit_forename.setText(user!!.firstName)
            user_edit_lastname.setText(user!!.lastName)
            user_edit_gender.setSelection(resources.getStringArray(R.array.genders_en).indexOf(user!!.gender))
            mUser = user
        })

        viewModel.account.observe(this, Observer { account ->
            mAccount = account!!
        })



        user_edit_new_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.isNotEmpty()){
                    user_new_password_layout.visibility = View.VISIBLE
                }else{
                    user_new_password_layout.visibility = View.GONE
                }
            }
        })

    }

    fun patch(){
        if(user_edit_password.text.isEmpty()){
            user_edit_password.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))
            return
        }

        patch_progress.visibility = View.VISIBLE
        user_settings_layout.isClickable = false
        user_settings_layout.alpha = 0.5f

        var passwordIsCorrect = false

        async {
            passwordIsCorrect = viewModel.checkPassword(mAccount.username!!,user_edit_password.text.toString())
            runOnUiThread{loginCallback(passwordIsCorrect)}
        }
    }

    fun checkNewPassword() : Boolean{
        if(user_edit_new_password.text.length < 8)
            return false
        if(!user_edit_new_password.text.toString().matches(Regex("[a-zA-Z0-9 \\D]")))
            return false
        return true
    }

    fun doPassword(){
        val account = Account()

        if(user_edit_new_password.text.toString().equals("")){
            if(!checkNewPassword()){
                password_conditions.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))
                return
            }

            if(!user_edit_new_password.text.equals(user_edit_new_password_repeat.text)) {
                user_edit_new_password_repeat.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))
                user_edit_new_password.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))
                return
            }

            account.id = mAccount.id
            account.newPassword = user_edit_new_password.text.toString()
            account.newPasswordRepeat = user_edit_new_password_repeat.text.toString()

            async { viewModel.patchAccount(account) }
        }
    }

    fun doUser(){
        val user = User()

        user.firstName = user_edit_forename.text.toString()
        user.lastName = user_edit_lastname.text.toString()
        if(user_edit_gender.selectedItemPosition != 0)
            user.gender = resources.getStringArray(R.array.genders_en)[
                    resources.getStringArray(R.array.genders_de).indexOf(user_edit_gender.selectedItem.toString())]
        user.email = user_edit_email.text.toString()
        user.id = mUser.id
        async { viewModel.patchUser(user) }
    }

    fun loginCallback(passwordIsCorrect: Boolean){
        if(passwordIsCorrect) {
            if (user_edit_new_password.text.isNotEmpty()) {
                mUser.email
                doPassword()
            }

            doUser()
        }

        runOnUiThread {
            patch_progress.visibility = View.INVISIBLE
            user_settings_layout.isClickable = true
            user_settings_layout.alpha = 1f
        }

        if(!passwordIsCorrect){
            runOnUiThread{user_edit_password.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))}
        }
    }
}