package org.schulcloud.mobile.ui.main;

import java.util.List;

import org.schulcloud.mobile.data.model.Ribot;
import org.schulcloud.mobile.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showRibots(List<Ribot> ribots);

    void showRibotsEmpty();

    void showError();

}
