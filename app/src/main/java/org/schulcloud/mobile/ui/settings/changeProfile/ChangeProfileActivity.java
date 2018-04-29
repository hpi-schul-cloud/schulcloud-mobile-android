package org.schulcloud.mobile.ui.settings.changeProfile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeProfileActivity extends BaseActivity<ChangeProfileMvpView,ChangeProfilePresenter>
implements ChangeProfileMvpView{

    private CurrentUser mCurrentUser;
    private ArrayAdapter<CharSequence> spinner_adapter;
    private boolean passwordIsOkay = false;
    private Animation animationScaleIn;
    private Animation animationScaleOut;
    private boolean oldPasswordEntered = false;

    @Inject
    ChangeProfilePresenter mChangeProfilePresenter;
    @Inject
    SettingsPresenter mSettingsPresenter;

    @BindView(R.id.change_profile_add_if_not_in_demo_mode)
    LinearLayout addIfNotDemoMode;
    @BindView(R.id.change_profile_gender_spinner)
    Spinner gender_spinner;
    @BindView(R.id.change_profile_email_EditText)
    EditText email_EditText;
    @BindView(R.id.change_profile_password_editText)
    EditText password_editText;
    @BindView(R.id.change_profile_newPassword_editText)
    EditText newPassword_editText;
    @BindView(R.id.change_profile_newPasswordRepeat_editText)
    EditText newPasswordRepeat_editText;
    @BindView(R.id.change_profile_submit)
    Button settings_submit;
    @BindView(R.id.change_profile_passwordsDoNotMatch)
    TextView passwordsDoNotMatch;
    @BindView(R.id.change_profile_passwordTooShort)
    TextView passwordTooShort;
    @BindView(R.id.change_profile_passwordControlFailed)
    TextView passwordControlFailed;
    @BindView(R.id.change_profile_passwordNeedsNumbers)
    TextView passwordNeedsNumbers;
    @BindView(R.id.passwordInfoLayout)
    LinearLayout passwordInfo;
    @BindView(R.id.change_profile_passwordOkay)
    TextView passwordOkay;
    @BindView(R.id.change_profile_oldPasswordInfoLayout)
    LinearLayout oldPasswordInfo;
    @BindView(R.id.change_profile_oldPasswordEmpty)
    TextView passwordEmpty;
    @BindView(R.id.settings_current_password)
    TextView currentPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_change_profile);
        ButterKnife.bind(this);
        setPresenter(mChangeProfilePresenter);

        animationScaleIn = AnimationUtils.loadAnimation(this,R.anim.resize_height_0_to_100);
        animationScaleIn.setFillAfter(true);
        animationScaleOut = AnimationUtils.loadAnimation(this,R.anim.resize_height_100_to_0);
        animationScaleOut.setFillAfter(true);
        settings_submit.setBackgroundColor(Color.GRAY);

        // Profile
        mChangeProfilePresenter.loadProfile();
        settings_submit.setOnClickListener(listener -> callProfileChange());

        TextWatcher listener = new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals("") && oldPasswordInfo.getChildCount() == 0){
                    oldPasswordInfo.addView(currentPasswordTextView);
                    oldPasswordInfo.addView(password_editText);
                    oldPasswordInfo.addView(passwordEmpty);
                    oldPasswordInfo.startAnimation(animationScaleIn);
                }else if(editable.toString().equals("") && oldPasswordInfo.getChildCount() != 0){
                    oldPasswordInfo.setAnimation(animationScaleOut);
                    oldPasswordInfo.animate().setDuration(700).withEndAction(() -> oldPasswordInfo.removeAllViews());
                }
                String newPassword = newPassword_editText.getText().toString();
                String newPasswordRepeat = newPasswordRepeat_editText.getText().toString();
                passwordIsOkay = checkPasswords(newPassword, newPasswordRepeat);
                checkPasswordStates();
            }
        };

        password_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                passwordEmpty.clearAnimation();
                if(editable.toString().equals("")){
                    oldPasswordInfo.addView(passwordEmpty);
                    passwordEmpty.startAnimation(animationScaleIn);
                    oldPasswordEntered = false;
                }else{
                    passwordEmpty.setAnimation(animationScaleOut);
                    passwordEmpty.animate().withEndAction(() -> {oldPasswordInfo.removeView(passwordEmpty);});
                    oldPasswordEntered = true;
                }
                checkPasswordStates();
            }
        });

        oldPasswordInfo.removeAllViews();

        newPassword_editText.addTextChangedListener(listener);
        newPasswordRepeat_editText.addTextChangedListener(listener);

        passwordInfo.removeAllViews();

        newPassword_editText.setHint(R.string.settings_newPasswordHint);
        newPasswordRepeat_editText.setHint(R.string.settings_newPasswordRepeatHint);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.genderArray,
                R.layout.item_gender_spinner);
        spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gender_spinner.setAdapter(spinner_adapter);

    }

    @Override
    public void callProfileChange(){
        if(oldPasswordEntered) {
            if(!passwordIsOkay && !newPassword_editText.getText().toString().equals(""))
                return;
            mCurrentUser = mChangeProfilePresenter.getCurrentUser();
            String name = mCurrentUser.getFirstName();
            String last_name = mCurrentUser.getLastName();
            String email = email_EditText.getText().toString() != null ?
                    email_EditText.getText().toString() : mCurrentUser.email;
            String gender = ArrayAdapter.createFromResource(this, R.array.genderArrayPosReference,
                    R.layout.item_gender_spinner)
                    .getItem(gender_spinner.getSelectedItemPosition()).toString();
            if (gender.equals("Choose Gender"))
                gender = null;
            String password = password_editText.getText().toString();
            String newPassword = newPassword_editText.getText().toString();
            mChangeProfilePresenter.changeProfile(name, last_name, email, gender, password, newPassword);
        }
    }

    @Override
    public boolean checkPasswords(String newPassword, String newPasswordRepeat){
        boolean passwordOkayBool = true;

        passwordInfo.removeAllViews();
        passwordsDoNotMatch.setText("");

        if(newPassword.equals("") || newPasswordRepeat.equals("")){
            return false;
        }

        if(!newPassword.equals(newPasswordRepeat)) {
            passwordInfo.addView(passwordsDoNotMatch);
            passwordsDoNotMatch.setText(R.string.settings_passwords_doNotMatch);
            passwordsDoNotMatch.startAnimation(animationScaleIn);
            passwordOkayBool = false;
        }

        if(newPassword.length() < 8) {
            passwordInfo.addView(passwordTooShort);
            passwordTooShort.startAnimation(animationScaleIn);
            passwordOkayBool = false;
        }

        if(Pattern.matches("[a-zA-Z]+",newPassword)){
            passwordInfo.addView(passwordNeedsNumbers);
            passwordNeedsNumbers.startAnimation(animationScaleIn);
            passwordOkayBool = false;
        }

        if(newPassword.equals(newPassword.toLowerCase())){
            passwordInfo.addView(passwordControlFailed);
            passwordControlFailed.startAnimation(animationScaleIn);
            passwordOkayBool = false;
        }

        if(passwordOkayBool == true) {
            passwordInfo.addView(passwordOkay);
            passwordOkay.startAnimation(animationScaleIn);
        }

        return passwordOkayBool;
    }

    @Override
    public void showProfile(CurrentUser user) {
        mCurrentUser = user;
        email_EditText.setText(mCurrentUser.email);
        List<String> genderReferenceArray =
                Arrays.asList(getResources().getStringArray(R.array.genderArrayPosReference));
        gender_spinner.setSelection(genderReferenceArray.indexOf(mCurrentUser.gender));
    }

    @Override
    public void showProfileError() {
        DialogFactory.createGenericErrorDialog(this, R.string.settings_profile_loading_error).show();
    }


    @Override
    public void showPasswordChangeFailed(){
        DialogFactory.createGenericErrorDialog(this,R.string.settings_showPasswordChangeFailed).show();
    }

    @Override
    public void showProfileChangeFailed(){
        DialogFactory.createGenericErrorDialog(this,R.string.settings_profile_changing_error).show();
    }

    @Override
    public void finishChange(){
        mSettingsPresenter.sendToView(v -> v.reloadProfile());
        finish();
    }

    @Override
    public void checkPasswordStates(){
        if(oldPasswordEntered && (passwordIsOkay || newPassword_editText.getText().toString().equals("")))
            settings_submit.setBackgroundColor(ContextCompat.getColor(this,R.color.hpiRed));
        else
            settings_submit.setBackgroundColor(Color.GRAY);
    }
}
