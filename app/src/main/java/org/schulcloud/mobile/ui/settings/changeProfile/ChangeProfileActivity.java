package org.schulcloud.mobile.ui.settings.changeProfile;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
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
    
    @Inject
    ChangeProfilePresenter mChangeProfilePresenter;

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

        // Profile
        mChangeProfilePresenter.loadProfile();
        settings_submit.setOnClickListener(listener -> {
            if(passwordIsOkay) {
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
        });

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View view, boolean hasFocus){
                    if(!hasFocus) {
                        String newPassword = newPassword_editText.getText().toString();
                        String newPasswordRepeat = newPasswordRepeat_editText.getText().toString();
                        passwordIsOkay = checkPasswords(newPassword, newPasswordRepeat);
                    }
                }
        };

        settings_submit.setTranslationX(newPasswordRepeat_editText.getX() + 20);
        newPassword_editText.setOnFocusChangeListener(listener);
        newPasswordRepeat_editText.setOnFocusChangeListener(listener);

        passwordInfo.removeAllViews();

        newPassword_editText.setHint(R.string.settings_newPasswordHint);
        newPasswordRepeat_editText.setHint(R.string.settings_newPasswordRepeatHint);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.genderArray,
                R.layout.item_gender_spinner);
        spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gender_spinner.setAdapter(spinner_adapter);

    }

    @Override
    public boolean checkPasswords(String newPassword, String newPasswordRepeat){
        boolean passwordOkayBool = true;

        if(newPassword == "" || newPasswordRepeat == ""){
            return false;
        }

        passwordInfo.removeAllViews();

        passwordsDoNotMatch.clearAnimation();
        if(!newPassword.equals(newPasswordRepeat)){
            passwordInfo.addView(passwordsDoNotMatch);
            passwordsDoNotMatch.startAnimation(animationScaleIn);
        }else{
            passwordOkayBool = false;
        }

        passwordTooShort.clearAnimation();
        if(newPassword.length() < 8){
            passwordInfo.addView(passwordTooShort);
            passwordTooShort.startAnimation(animationScaleIn);
        }else{
            passwordOkayBool = false;
        }

        passwordNeedsNumbers.clearAnimation();
        if(Pattern.matches("[a-zA-Z]+",newPassword)){
            passwordInfo.addView(passwordNeedsNumbers);
            passwordNeedsNumbers.startAnimation(animationScaleIn);
        }else{
            passwordOkayBool = false;
        }

        passwordControlFailed.clearAnimation();
        if(newPassword.equals(newPassword.toLowerCase())){
            passwordInfo.addView(passwordControlFailed);
            passwordControlFailed.startAnimation(animationScaleIn);
        }else{
            passwordOkayBool = false;
        }

        if(this.passwordIsOkay == true) {
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
    public void showChangeSuccess(){
        DialogFactory.createProgressDialog(this,R.string.settings_profileChangeSuccess).show();
        mChangeProfilePresenter.loadProfile();
        finish();
    }

    @Override
    public void showProfileChangeFailed(){
        DialogFactory.createGenericErrorDialog(this,R.string.settings_profile_changing_error).show();
    }
}
