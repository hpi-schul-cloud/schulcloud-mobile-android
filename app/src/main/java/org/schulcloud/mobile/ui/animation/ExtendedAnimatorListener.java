package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.support.annotation.NonNull;

public class ExtendedAnimatorListener implements Animator.AnimatorListener {
    private Runnable mActionStart;
    private Runnable mActionEnd;
    private Runnable mActionRepeat;
    private Runnable mActionCancel;

    public ExtendedAnimatorListener( Runnable actionStart, Runnable actionEnd, Runnable actionCancel, Runnable actionRepeat){
        mActionStart = actionStart == null?() -> {return;}:actionStart;
        mActionEnd = actionEnd == null?() -> {return;}:actionEnd;
        mActionCancel = actionCancel == null?() -> {return;}:actionCancel;
        mActionRepeat = actionRepeat == null?() -> {return;}:actionRepeat;
    }

    public Runnable getActionStart(){
        return mActionStart;
    }

    public Runnable getActionEnd() {
        return mActionEnd;
    }

    public Runnable getActionCancel() {
        return mActionCancel;
    }

    public Runnable getActionRepeat() {
        return mActionRepeat;
    }

    public void setActionStart(@NonNull Runnable actionStart){
        mActionStart = actionStart;
    }

    public void setActionEnd(@NonNull Runnable actionEnd){
        mActionStart = actionEnd;
    }

    public void setActionCancel(@NonNull Runnable actionCancel){
        mActionStart = actionCancel;
    }

    public void setActionRepeat(@NonNull Runnable actionRepeat){
        mActionStart = actionRepeat;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        mActionStart.run();
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        mActionEnd.run();
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        mActionCancel.run();
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        mActionRepeat.run();
    }
}
