package org.schulcloud.mobile.ui.signin;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SignInActivity extends BaseActivity<SignInMvpView, SignInPresenter>
        implements SignInMvpView {

    @Inject
    SignInPresenter mSignInPresenter;

    @BindView(R.id.input_username)
    EditText username;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.btn_demo_student)
    Button demoStudent;
    @BindView(R.id.btn_demo_teacher)
    Button demoTeacher;
    @BindView(R.id.forgot_password)
    Button forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setPresenter(mSignInPresenter);

        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        login.setOnClickListener(v -> mSignInPresenter
                .signIn(username.getText().toString(), password.getText().toString(), false));

        demoStudent.setOnClickListener(v -> mSignInPresenter.signIn(
                getString(R.string.login_demo_student_username),
                getString(R.string.login_demo_student_password), true));
        demoTeacher.setOnClickListener(v -> mSignInPresenter.signIn(
                getString(R.string.login_demo_teacher_username),
                getString(R.string.login_demo_teacher_password), true));
        forgotPassword.setOnClickListener(v -> {
            openPasswordRecovery();
        });
    }


    /***** MVP View methods implementation *****/
    @Override
    public void showSignInSuccessful() {
        DialogFactory
                .createSimpleOkErrorDialog(this, R.string.login_title, R.string.login_successful)
                .show();
        startActivity(new Intent(this, MainActivity.class));
    }
    @Override
    public void showSignInFailed() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.login_error))
                .show();
    }

    public void openPasswordRecovery() {
        WebView pwRecoveryView = new WebView(this);
        WebSettings pwRecoverySettings = pwRecoveryView.getSettings();
        pwRecoverySettings.setJavaScriptEnabled(true);
        pwRecoveryView.loadUrl("http://schul-cloud.org");
        setContentView(pwRecoveryView);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
            pwRecoveryView.evaluateJavascript("ADD JAVASCRIPT",null);
        }else{
            pwRecoveryView.loadUrl("javascript: ADD JAVASCRIPT");
        }
    }

    @Override
    public void goToSignIn() {
        // obsolete in SignInActivity
    }
}
