package org.schulcloud.mobile.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.main.MainActivity;
import org.schulcloud.mobile.ui.signin.scloudWeb.ScloudWebClient;
import org.schulcloud.mobile.ui.signin.scloudWeb.ScloudWebView;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        ScloudWebView pwRecoveryView = new ScloudWebView(this);
        ScloudWebClient webClient = new ScloudWebClient();
        pwRecoveryView.setWebViewClient(webClient);
        WebSettings pwRecoverySettings = pwRecoveryView.getSettings();
        pwRecoverySettings.setJavaScriptEnabled(true);
        pwRecoveryView.loadUrl("http://schul-cloud.org");
        setContentView(pwRecoveryView);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
            pwRecoveryView.evaluateJavascript("document.getElementById(\"submit-pwrecovery\").click()",null);
        }else{
            pwRecoveryView.loadUrl("javascript: document.getElementById(\"submit-pwrecovery\").click()");
        }
    }

    @Override
    public void goToSignIn() {
        // obsolete in SignInActivity
    }
}
