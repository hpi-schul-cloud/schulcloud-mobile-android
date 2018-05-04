package org.schulcloud.mobile.ui.settings.changeProfile;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;
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
    private ArrayList<AnimationLogic> animationLogics = new ArrayList<AnimationLogic>();

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

        // Animation Logic
        ViewGroup parent = (ViewGroup)findViewById(R.id.change_profile_newPassword_Layout);

        animationLogics.add(new AnimationLogic(oldPasswordInfo,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(0).setLogic(() -> (!newPassword_editText.getText().toString().equals("") || !newPasswordRepeat_editText.getText().toString().equals(""))? true : false);

        animationLogics.add(new AnimationLogic(passwordEmpty,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(1).setLogic(() -> (password_editText.getText().toString().equals(""))?true:false);

        animationLogics.add(new AnimationLogic(passwordInfo,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(2).setLogic(() -> (!newPassword_editText.getText().toString().equals("") || !newPasswordRepeat_editText.getText().toString().equals(""))? true : false);

        animationLogics.add(new AnimationLogic(passwordsDoNotMatch,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(3).setLogic(() -> (!newPassword_editText.getText().equals(newPasswordRepeat_editText.getText())?true:false));

        animationLogics.add(new AnimationLogic(passwordTooShort,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(4).setLogic(() -> (newPassword_editText.getText().toString().length() < 8)?true:false);

        animationLogics.add(new AnimationLogic(passwordNeedsNumbers,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(5).setLogic(() -> (Pattern.matches("[a-zA-Z]+",newPassword_editText.getText().toString()))?true:false);

        animationLogics.add(new AnimationLogic(passwordControlFailed,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(6).setLogic(() -> (newPassword_editText.equals(newPassword_editText.getText().toString().toLowerCase()))?true:false);

        animationLogics.add(new AnimationLogic(passwordOkay,parent,animationScaleIn,animationScaleOut));
        animationLogics.get(7).setLogic(() -> (passwordIsOkay));

        // Profile
        mChangeProfilePresenter.loadProfile();
        settings_submit.setOnClickListener(listener -> callProfileChange());

        //TODO: clean up logic

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

        parent.removeView(oldPasswordInfo);

        password_editText.addTextChangedListener(listener);
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

        for(int i = 0; i < animationLogics.size(); i++){
            try {
                animationLogics.get(i).checkLogic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Submit button
        if(oldPasswordEntered && (passwordIsOkay || newPassword_editText.getText().toString().equals("")))
            settings_submit.setBackgroundColor(ContextCompat.getColor(this,R.color.hpiRed));
        else
            settings_submit.setBackgroundColor(Color.GRAY);
    }

    public class AnimationLogic{
        View mView;
        ViewGroup mViewParent;
        Callable<Boolean> mLogic;
        Runnable mActionEnd;
        Runnable mActionStart;
        Runnable mActionRepeat;
        Runnable mActionAdd;
        Runnable mActionRemove;

        Animation mTransIn;
        Animation mTransOut;

        public AnimationLogic(View view,ViewGroup parent, Animation transIn, Animation transOut){
            mView = view;
            mViewParent = parent;
            mTransIn = transIn;
            mTransOut = transOut;

            mActionAdd =  () -> parent.addView(view);
            mActionRemove = () -> parent.removeView(view);

            mTransIn.setAnimationListener(new AnimationListener(null,null,null));
            mTransOut.setAnimationListener(new AnimationListener(null,null, mActionRemove));

        }

        public void setLogic(Callable<Boolean> logic){
            mLogic = logic;
        }

        public void setActionAdd(Runnable actionAdd){
            mActionAdd = actionAdd;
        }

        public void setActionRemove(Runnable actionRemove){
            mActionRemove = actionRemove;
        }

        public void checkLogic() throws Exception {
            if(mLogic.call()){
                if(mViewParent.findViewById(mView.getId()) == null){
                    mActionAdd.run();
                    mView.startAnimation(mTransOut);
                }
            }else{
                mView.startAnimation(mTransOut);
            }
        }
    }

    public class AnimationListener implements Animation.AnimationListener{
        private Runnable mActionStart;
        private Runnable mActionRepeat;
        private Runnable mActionEnd;

        public AnimationListener(@Nullable Runnable actionStart, @Nullable Runnable actionRepeat, @Nullable Runnable actionEnd){
            mActionStart = actionStart != null ? actionStart : () -> {return;};
            mActionRepeat = actionRepeat != null ? actionRepeat : () -> {return;};
            mActionEnd = actionEnd != null ? actionEnd : () -> {return;};
        }

        @Override
        public void onAnimationStart(Animation animation) {
            mActionStart.run();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mActionEnd.run();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            mActionRepeat.run();
        }
    }
}