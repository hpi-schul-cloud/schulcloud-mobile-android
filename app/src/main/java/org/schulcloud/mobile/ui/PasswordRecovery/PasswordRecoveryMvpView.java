package org.schulcloud.mobile.ui.PasswordRecovery;

        import org.schulcloud.mobile.data.model.Account;
        import org.schulcloud.mobile.data.model.responseBodies.ResetResponse;
        import org.schulcloud.mobile.ui.base.MvpView;

public interface PasswordRecoveryMvpView  extends MvpView {
    void showSuccessMessage();
    void showErrorMessage(boolean subStep);
    void setStep(int step);
    void sendEmail(ResetResponse responseBody, Account account);
}
