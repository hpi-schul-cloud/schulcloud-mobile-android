package org.schulcloud.mobile.ui.signin.scloudWeb;

import android.content.Context;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.Button;

import org.schulcloud.mobile.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScloudWebView extends WebView {

    @BindView(R.id.webclient_exit)
    Button exit;

    public ScloudWebView(Context context){
        super(context);
        this.inflate(context,R.layout.web_client,null);
        ButterKnife.bind(this);
        exit.setOnClickListener(l ->{
            removeView(this);
        });
    }
    
}
