package org.schulcloud.mobile.ui.scloudWeb;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.ui.base.BasePresenter;

import javax.inject.Inject;

public class ScloudWebPresenter extends BasePresenter<ScloudWebMvpView>{
    private final DataManager mDataManager;

    @Inject
    public ScloudWebPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }
}
