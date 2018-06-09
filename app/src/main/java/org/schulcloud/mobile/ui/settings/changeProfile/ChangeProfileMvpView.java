package org.schulcloud.mobile.ui.settings.changeProfile;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.ui.base.MvpView;

public interface ChangeProfileMvpView extends MvpView {

    void showProfile(@NonNull CurrentUser user);

    void showProfileError();

    void showPasswordChangeFailed();

    void showProfileChangeFailed();

    void checkPasswordStates();

    void callProfileChange();

    void finishChange();

    void checkAnimationLogic();

}