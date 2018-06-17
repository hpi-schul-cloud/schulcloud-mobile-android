package org.schulcloud.mobile.ui.settings.changeProfile;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.ui.animation.AnimationLogicListener;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeProfileActivity extends BaseActivity<ChangeProfileMvpView,ChangeProfilePresenter>
implements ChangeProfileMvpView{

    private CurrentUser mCurrentUser;
    private ArrayAdapter<CharSequence> spinner_adapter;
    private boolean passwordIsOkay = false;
    private boolean oldPasswordEntered = false;
    private ArrayList<AnimationLogicListener> animationLogics = new ArrayList<AnimationLogicListener>();

    @Inject
    ChangeProfilePresenter mChangeProfilePresenter;
    @Inject
    SettingsPresenter mSettingsPresenter;

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

        Animation animIn;
        Animation animOut;

        settings_submit.setBackgroundColor(Color.GRAY);

        // Animation Logic
        new profileAnimationLogicListener(oldPasswordInfo,animIn,animOut)
                .setLogic(() -> (!newPassword_editText.getText().toString().equals("") || !newPasswordRepeat_editText.getText().toString().equals(""))?true:false);

        /*new profileAnimationLogicListener(passwordEmpty,animIn,animOut)
                .setLogic(() -> password_editText.getText().toString().equals("")?true:false);*/

        new profileAnimationLogicListener(passwordInfo,animIn,animOut)
                .setLogic(() -> (!newPassword_editText.getText().toString().equals(""))?true:false);

        /*new profileAnimationLogicListener(passwordsDoNotMatch,animIn,animOut)
                .setLogic(() -> (!newPassword_editText.getText().toString().equals(newPasswordRepeat_editText.getText().toString()))?true:false);

        new profileAnimationLogicListener(passwordTooShort,animIn,animOut)
                .setLogic(() -> newPassword_editText.getText().toString().length() < 8?true:false);

        new profileAnimationLogicListener(passwordNeedsNumbers,animIn,animOut)
                .setLogic(() -> Pattern.matches("[a-zA-Z]+", newPassword_editText.getText().toString())?true:false);

        new profileAnimationLogicListener(passwordControlFailed,animIn,animOut)
                .setLogic(() -> newPassword_editText.getText().toString().equals(newPassword_editText.getText().toString().toLowerCase())?true:false);

        new profileAnimationLogicListener(passwordOkay,animIn,animOut)
                .setLogic(() -> (passwordIsOkay));*/

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
                checkAnimationLogic();
            }
        };

        password_editText.addTextChangedListener(listener);
        newPassword_editText.addTextChangedListener(listener);
        newPasswordRepeat_editText.addTextChangedListener(listener);

        //passwordInfo.removeAllViews();
        ViewGroup oldPasswordParent = (ViewGroup)oldPasswordInfo.getParent();
        oldPasswordParent.removeView(oldPasswordInfo);
        ViewGroup passwordInfoParent = (ViewGroup) passwordInfo.getParent();
        passwordInfoParent.removeView(passwordInfo);

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
    }

    @Override
    public void checkAnimationLogic(){

        //Submit button
        if(oldPasswordEntered && (passwordIsOkay || newPassword_editText.getText().toString().equals("")))
            settings_submit.setBackgroundColor(ContextCompat.getColor(this,R.color.hpiRed));
        else
            settings_submit.setBackgroundColor(Color.GRAY);
    }

    public class profileAnimationLogicListener extends AnimationLogicListener {

        public profileAnimationLogicListener(View view, Animation transIn, Animation transOut) {
            super(view, transIn, transOut);
            //transIn.getChildAnimations();
            setActionIn(() -> {if(mViewParent.findViewById(mView.getId()) == null) {mViewParent.addView(mView);}});
            setActionOut(() -> {mViewParent.removeView(mView);});
        }

        @Override
        public void checkLogic() throws Exception {
            if(mLogic.call()){
                if(!animListenerIn.mInfo.wasStarted){
                    mView.clearAnimation();
                    mView.startAnimation(mTransIn);
                }
            }else{
                if(animListenerIn.mInfo.wasStarted && !animListenerOut.mInfo.wasStarted){
                    mView.clearAnimation();
                    mView.startAnimation(mTransOut);
                }
            }
        }
    }

}