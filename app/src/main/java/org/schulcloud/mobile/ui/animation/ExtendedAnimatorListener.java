package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Debug;
import android.support.annotation.NonNull;

public class ExtendedAnimatorListener extends AnimatorListenerAdapter {
    private Runnable mActionStart;
    private Runnable mActionEnd;
    private Runnable mActionRepeat;
    private Runnable mActionCancel;

    public ExtendedAnimatorListener( Runnable actionStart, Runnable actionEnd){
        mActionStart = actionStart == null?() -> {return;}:actionStart;
        mActionEnd = actionEnd == null?() -> {return;}:actionEnd;
    }

    public Runnable getActionStart(){
        return mActionStart;
    }

    public Runnable getActionEnd() {
        return mActionEnd;
    }

    public void setActionStart(@NonNull Runnable actionStart){
        mActionStart = actionStart;
    }

    public void setActionEnd(@NonNull Runnable actionEnd){
        mActionStart = actionEnd;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        super.onAnimationStart(animator);
        mActionStart.run();
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        //Debug.waitForDebugger();
        super.onAnimationEnd(animator);
        if(!animator.isRunning())
            mActionEnd.run();
    }
}