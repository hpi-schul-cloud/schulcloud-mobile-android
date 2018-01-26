package org.schulcloud.mobile.ui.signin.passwordRecovery;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.ui.base.BaseFragment;
import org.schulcloud.mobile.util.DialogFactory;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordRecoveryFragment extends BaseFragment implements PasswordRecoveryMvpView {

    @Inject
    PasswordRecoveryPresenter mpwrPresenter;

    @BindView(R.id.pwRecUsername)
    EditText usernameEditText;
    @BindView(R.id.pwRecSubmit)
    Button submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_recovery,container,false);
        ButterKnife.bind(this,view);

        mpwrPresenter.attachView(this);

        submit.setOnClickListener(listener ->{
            mpwrPresenter.sendPasswordRecovery(usernameEditText.getText().toString());
        });

        return view;
    }

    @Override
    public void showPasswordRecovery() {
        DialogFactory
                .createSimpleOkErrorDialog(getContext(),R.string.passwordRecovery,R.string.passwordRecoverySuccessfull)
                .show();
    }

    @Override
    public void showPasswordRecoveryFailed() {
        DialogFactory
                .createGenericErrorDialog(getContext(), R.string.passwordRecoveryFailed)
                .show();
    }

}
