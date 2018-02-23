package org.schulcloud.mobile.ui.signin.scloudWeb;


import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.util.dialogs.DialogFactory;

public class ScloudWebClient extends WebViewClient {


    public ScloudWebClient(){
        super();
    }

    @Override
    public void onLoadResource(WebView view, String url){
        if(url != "https://schul-cloud.org/*")
            DialogFactory.createGenericErrorDialog(view.getContext() ,R.string.client_wrong_url);
        else
            super.onLoadResource(view,url);
    }

}
