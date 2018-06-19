package org.schulcloud.mobile.ui.animation;

import android.view.View;
import android.view.animation.Animation;

public class AnimationClass {
    public Animation mAnim;
    public InfObject mInf;
    public View mView;

    public AnimationClass(Animation anim, View view){
        mAnim = anim;
        mView = view;
        mInf = new InfObject();
    }

    public void start(){
        mInf.wasStarted = true;
        mInf.isFinished = false;
        mView.startAnimation(mAnim);
    }

    public void reset(){
        mInf.wasStarted = false;
        mInf.isFinished = true;
        mAnim.reset();
    }

    public boolean wasStarted(){
        return mInf.wasStarted;
    }

    public boolean isFinished(){
        return mInf.isFinished;
    }

    public class InfObject {
        public boolean wasStarted = false;
        public boolean isFinished = true;
    }
}
