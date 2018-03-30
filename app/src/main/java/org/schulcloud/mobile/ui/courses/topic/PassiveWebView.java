package org.schulcloud.mobile.ui.courses.topic;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import org.schulcloud.mobile.ui.common.ContentWebView;

/**
 * A {@link WebView} that does not consume touch events.
 * <p>
 * Date: 2/22/2018
 */
public class PassiveWebView extends ContentWebView {
    public PassiveWebView(@NonNull Context context) {
        super(context, null);
        init();
    }
    public PassiveWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }
    public PassiveWebView(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @RequiresApi(21)
    public PassiveWebView(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
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
