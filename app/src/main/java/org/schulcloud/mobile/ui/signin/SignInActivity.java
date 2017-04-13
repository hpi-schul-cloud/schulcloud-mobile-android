package org.schulcloud.mobile.ui.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.util.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity implements SignInMvpView {

    @Inject SignInPresenter mSignInPresenter;

    @BindView(R.id.signin) Button signIn;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mSignInPresenter.attachView(this);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInPresenter.signIn();
            }
        });
    }


    @Override
    protected void onDestroy() {
        mSignInPresenter.detachView();
        super.onDestroy();
    }


    @Override
    public void showSignInSuccessful() {
        DialogFactory.createGenericErrorDialog(this, "HEY FICKER EINGELOGGT!")
                .show();
    }

    @Override
    public void showSignInFailed() {
        DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_ribots))
                .show();
    }
}
