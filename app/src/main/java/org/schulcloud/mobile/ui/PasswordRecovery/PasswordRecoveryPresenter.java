package org.schulcloud.mobile.ui.PasswordRecovery;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Account;
import org.schulcloud.mobile.data.model.responseBodies.ResetResponse;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryMvpView> {
    private final UserDataManager mUserDataManager;
    private Account resetAccount = null;
    private ResetResponse resetResponse = null;

    @Inject
    public PasswordRecoveryPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }

    public void requestReset(@NonNull String email) {
        mUserDataManager.requestResetPassword(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> sendToView(view -> {
                            resetResponse = responseBody;
                            mUserDataManager.getAccount(responseBody.account)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(response -> {
                                        resetAccount = response;
                                        view.sendEmail(responseBody, getAccount());
                                        view.showSuccessMessage();
                                        view.setStep(1);
                                    }, error -> System.out.print(error.getMessage()));
                        }),
                        error -> sendToView(view -> view.showErrorMessage(false)));
    }

    public void setPassword(String password) {
        mUserDataManager.resetPassword(resetAccount._id, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> sendToView(view -> {
                    view.setStep(3);
                    view.showSuccessMessage();
                }), error -> sendToView(view -> view.showErrorMessage(false)));
    }

    public void checkCode(String code) {
        final String rightCode = resetResponse._id;
        sendToView(v -> {
            if (code.equals(rightCode)) {
                v.showSuccessMessage();
                v.setStep(2);
            } else
                v.showErrorMessage(false);
        });
    }
    public boolean passwordsEqual(String pw1, String pw2) {
        return TextUtils.equals(pw1, pw2);
    }
    public boolean isNewPasswortValid(String passwort){
        int pwSize = passwort.length();
        int capChars = charsInInterval(passwort,'A','Z');
        int smallChars = charsInInterval(passwort,'a','z');
        int digits = charsInInterval(passwort,'0','9');
        int specialChars = pwSize - capChars - smallChars - digits;

        if (pwSize < 8)
            return false;
        if (capChars < 1)
            return false;
        if (smallChars < 1)
            return false;
        if (digits < 1)
            return false;
        if (specialChars < 1)
            return false;

        return true;
    }
    private int charsInInterval(String pw,char min,char max){
        int amount = 0;
        for(char c : pw.toCharArray()){
            if(c>=min && c<=max){
                amount++;
            }
        }
        return amount;
    }

    private Account getAccount(){
        return this.resetAccount;
    }
}
