package org.schulcloud.mobile.ui.main;

import java.util.List;

import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showUsers(List<User> users);

    void showUsersEmpty();

    void showError();

}
