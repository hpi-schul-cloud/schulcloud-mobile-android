package org.schulcloud.mobile.controllers.settings

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_user_settings.*
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.toast
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.base.BaseActivity
import org.schulcloud.mobile.jobs.base.RequestJobCallback
import org.schulcloud.mobile.models.user.Account
import org.schulcloud.mobile.models.user.User
import org.schulcloud.mobile.viewmodels.SettingsViewModel
import org.schulcloud.mobile.viewmodels.UserSettingsViewModel
import java.util.*

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
    private lateinit var genderReferences: Array<String>
    private var stage = 0
    private var callback = object: RequestJobCallback(){
        override fun onError(code: ErrorCode) {
            stage += 1
            handlePatch(stage,true)
        }
        override fun onSuccess() {
            stage += 1
            handlePatch(stage,false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserSettingsViewModel::class.java)
        setContentView(R.layout.activity_user_settings)
        genderReferences = resources.getStringArray(R.array.genders)
        populateSpinner()
        populateView()
        user_edit_submit.setOnClickListener({ async{ handlePatch(stage,false)} })

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


    fun checkNewPassword() : Boolean{
        if(user_edit_new_password.text.length < 8)
            return false^
        if(!user_edit_new_password.text.toString().matches(Regex("[a-zA-Z0-9 ]")))
            return false
        return true
    }

    fun handlePatch(stage: Int,error: Boolean){
        when(stage){
            0 -> {
                if(user_edit_new_password.text.toString() != "") {
                    if (!checkNewPassword()) {
                        password_conditions.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                        return
                    }

                    if (!user_edit_new_password.text.equals(user_edit_new_password_repeat.text)) {
                        user_edit_new_password_repeat.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                        user_edit_new_password.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                        return
                    }
                }

                if(user_edit_password.text.toString() == ""){
                    user_edit_password.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake))
                    return
                }else{
                    patch_progress.visibility = View.VISIBLE
                    user_settings_layout.alpha = 0.5f
                    viewModel.checkPassword(mAccount.username!!,user_edit_password.text.toString(),callback)
                }

            }
            1 -> {
                if(error) {
                    user_edit_password.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
                    reset()
                    return
                }
                val user = User()
                user.firstName = user_edit_forename.text.toString()
                user.lastName = user_edit_lastname.text.toString()
                user.gender = if(user_edit_gender.selectedItemPosition == 0) null else genderReferences[user_edit_gender.selectedItemPosition]
                user.email = user_edit_email.text.toString()
                user.id = mUser.id
                async{viewModel.patchUser(user,callback)}
            }
            2 -> {
                if(error){
                    reset()
                    toast(R.string.user_something_went_wrong)
                    return
                }
                var account = Account()
                account.id = mAccount.id
                account.newPassword = user_edit_new_password.text.toString()
                account.newPasswordRepeat = user_edit_new_password_repeat.text.toString()
                async{viewModel.patchAccount(account,callback)}
            }
            3 -> {
                if(error){
                    toast(R.string.user_something_went_wrong)
                }else{
                    toast(R.string.user_patch_successful)
                }
                patch_progress.visibility = View.GONE
                user_settings_layout.alpha = 1.0f
                viewModel.user.observe(this, Observer { user ->
                    mUser = user!!
                })
                viewModel.account.observe(this,Observer { account ->
                  mAccount = account!!
                })
                finish()
            }
        }
    }

    fun reset(){
        patch_progress.visibility = View.GONE
        user_settings_layout.alpha = 1.0f
        stage = 0
    }

    fun populateSpinner(){
        var strings: MutableList<String> = mutableListOf()
        for(item: Int in viewModel.genderIds){
            strings.add(resources.getString(item))
        }
        var spinnerAdapter = ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,strings)
        user_edit_gender.adapter = spinnerAdapter
    }

    fun populateView(){
        viewModel.user.observe(this, Observer { user ->
            user_edit_email.setText(user!!.email)
            user_edit_forename.setText(user!!.firstName)
            user_edit_lastname.setText(user!!.lastName)
            user_edit_gender.setSelection(genderReferences.indexOf(user.gender))
            mUser = user
        })

        viewModel.account.observe(this, Observer { account ->
            mAccount = account!!
        })
    }
}