package org.schulcloud.mobile.ui.homework.detailed.details;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.ui.base.MvpView;

/**
 * Date: 4/27/2018
 */
interface DetailsMvpView extends MvpView {

    void showDescription(@NonNull String description);

}
