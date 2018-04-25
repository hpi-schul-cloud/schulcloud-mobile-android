package org.schulcloud.mobile.ui.settings.changeProfile;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.ui.base.MvpView;

public interface ChangeProfileMvpView extends MvpView {

    void showProfile(@NonNull CurrentUser user);

    void showProfileError();

    void showPasswordChangeFailed();

    void showChangeSuccess();

    void showProfileChangeFailed();

    boolean checkPasswords(String newPassword, String newPasswordRepeat);

    void checkPasswordStates();

}
