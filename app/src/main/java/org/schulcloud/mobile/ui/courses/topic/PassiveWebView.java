package org.schulcloud.mobile.ui.courses.topic;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * A {@link WebView} that does not consume touch events.
 * <p>
 * Date: 2/22/2018
 */
public class PassiveWebView extends WebView {
    public PassiveWebView(Context context) {
        super(context);
        init();
    }
    public PassiveWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public PassiveWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @RequiresApi(21)
    public PassiveWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init() {
        setClickable(false);
        setFocusable(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
