package org.schulcloud.mobile.ui.settings.changeProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.SettingsActivity;
import org.schulcloud.mobile.util.ViewUtil;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeProfileActivity extends BaseActivity<ChangeProfileMvpView,ChangeProfilePresenter>
implements ChangeProfileMvpView{

    private CurrentUser mCurrentUser;
    private ArrayAdapter<CharSequence> spinner_adapter;
    
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_change_profile);
        ButterKnife.bind(this);
        setPresenter(mChangeProfilePresenter);


        // Profile
        mChangeProfilePresenter.loadProfile();
        settings_submit.setOnClickListener(listener -> {
            mCurrentUser = mChangeProfilePresenter.getCurrentUser();
            String name = mCurrentUser.getFirstName();
            String last_name = mCurrentUser.getLastName();
            String email = email_EditText.getText().toString() != null?
                    email_EditText.getText().toString() : mCurrentUser.email;
            String gender = ArrayAdapter.createFromResource(this, R.array.genderArrayPosReference,
                    R.layout.item_gender_spinner)
                    .getItem(gender_spinner.getSelectedItemPosition()).toString();
            if(gender.equals("Choose Gender"))
                gender = null;
            String password = password_editText.getText().toString();
            String newPassword = newPassword_editText.getText().toString();
            String newPasswordRepeat = newPasswordRepeat_editText.getText().toString();
            mChangeProfilePresenter.changeProfile(name,last_name,email,gender,password,newPassword,
                    newPasswordRepeat);
        });
        newPassword_editText.setHint(R.string.settings_newPasswordHint);
        newPasswordRepeat_editText.setHint(R.string.settings_newPasswordRepeatHint);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.genderArray,
                R.layout.item_gender_spinner);
        spinner_adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        gender_spinner.setAdapter(spinner_adapter);

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
    public void showPasswordBad(){
        DialogFactory.createGenericErrorDialog(this,R.string.settings_passwordControlFailed).show();
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
