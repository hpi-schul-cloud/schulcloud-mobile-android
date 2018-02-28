package org.schulcloud.mobile.ui.scloudWeb;

import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toolbar;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.injection.component.ActivityComponent;
import org.schulcloud.mobile.ui.base.BaseActivity;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ScloudWebActivtiy extends BaseActivity<ScloudWebMvpView,ScloudWebPresenter>{

    @BindView(R.id.web_toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.webclient_exit)
    Button button;

    @Inject
    ScloudWebPresenter mScloudWebPresenter;

    @Override
    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        activityComponent().inject(this);
        setPresenter(mScloudWebPresenter);
        setContentView(R.layout.web_client);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        WebView pwRecoveryView = new WebView(this);
        ScloudWebClient webClient = new ScloudWebClient();
        pwRecoveryView.setWebViewClient(webClient);
        WebSettings pwRecoverySettings = pwRecoveryView.getSettings();

        pwRecoverySettings.setJavaScriptEnabled(true);
        pwRecoverySettings.setDomStorageEnabled(true);
        pwRecoveryView.loadUrl("http://schul-cloud.org");
        setContentView(pwRecoveryView);
        ValueCallback<String> result = new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Timber.i("VALUE RECIEVED" + value);
            }
        };
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
            pwRecoveryView.evaluateJavascript("$(\"container\").trigger(\"submit-pwrecovery\")",result);
        }else{
            pwRecoveryView.loadUrl("javascript: $(\"container\").trigger(\"submit-pwrecovery\")");
        }
    }
}
