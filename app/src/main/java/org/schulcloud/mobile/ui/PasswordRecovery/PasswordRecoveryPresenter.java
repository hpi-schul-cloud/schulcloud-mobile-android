package org.schulcloud.mobile.ui.PasswordRecovery;


import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Account;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.data.model.requestBodies.FeedbackRequest;
import org.schulcloud.mobile.data.model.responseBodies.ResetResponse;
import org.schulcloud.mobile.data.sync.UserSyncService;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

@ConfigPersistent
public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryMvpView> {
    private final UserDataManager mUserDataManager;
    private Subscription mSubscription;
    private Account resetAccount = null;
    private ResetResponse resetResponse = null;


    @Inject
    public PasswordRecoveryPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
    }


    public void requestReset(@NonNull String username) {


            mUserDataManager.requestResetPassword(username)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            responseBody -> sendToView(view -> {
                                resetResponse = responseBody;
                                mUserDataManager.getAccount(responseBody.account).observeOn(AndroidSchedulers.mainThread()).subscribe(response ->{
                                this.resetAccount = response;
                                view.sendEmail(responseBody,getAccount());
                                view.showSuccessMessage();
                                view.setStep(1);
                                },error->{
                                    System.out.print(error.getMessage());
                                });

                            }),
                            error -> {
                                sendToView(PasswordRecoveryMvpView::showErrorMessage);

                            });


    }

    public void setPasswort(String AccountId,String passwort){

        mUserDataManager.resetPassword(AccountId,passwort)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> sendToView(view ->{
                    view.showSuccessMessage();
                    view.setStep(5);
                }), error -> sendToView(view ->{
                    view.showErrorMessage();
                }));
    }

    public void checkCode(String code){
        final String rightCode = resetResponse._id;
                sendToView(v->{
                if(code.equals(rightCode)){
                    v.showSuccessMessage();
                    v.setStep(2);
                }else{
                    v.showErrorMessage();
                }
            });



    }
    public boolean passwordsEqual(String pw1,String pw2) {
            return (pw1.equals(pw2));
    }
    public boolean isNewPasswortValid(String passwort){
        int pwSize = passwort.length();
        int capChars = charsInInterval(passwort,'A','Z');
        int smallChars = charsInInterval(passwort,'a','z');
        int specialChars = pwSize - capChars - smallChars;

        if(pwSize<8){
            return false;
        }
        if(capChars<1){
            return false;
        }
        if(smallChars<1){
            return false;
        }
        if(specialChars<1){
            return false;
        }

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
