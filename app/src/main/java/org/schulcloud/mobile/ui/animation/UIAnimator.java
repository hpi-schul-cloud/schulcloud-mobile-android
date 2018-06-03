package org.schulcloud.mobile.ui.animation;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.renderscript.Sampler;

public class UIAnimator extends ValueAnimator {
    public boolean isFinished = false;

    public UIAnimator(){
        super();
    }

    @Override
    public void start(){
        super.start();
        isFinished = true;
    }

    public class updateListener implements AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if(getCurrentPlayTime() == getDuration()){
                isFinished = true;
            }
        }
    }

    public static UIAnimator ofPropertyValuesHolder(PropertyValuesHolder... values) {
        UIAnimator anim = new UIAnimator();
        anim.setValues(values);
        return anim;
    }


}
