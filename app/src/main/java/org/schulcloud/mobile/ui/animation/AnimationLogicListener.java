package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Debug;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.Callable;

/**
 * This class is usable for conditional animations, in terms of animations which will be triggered
 * under certain circumstances. I.e. variable equals one, make button red.
 *
 * To use this class you simply have to initialize it and give it logic to work with via setLogic().
 * An example would be following
 *
 * new AnimationLogicListener(fooView,fooAnimatorIn,fooAnimatorOut).setLogic(() -> (bar==1)?true:false);
 *
 * Upon setting it, it will start iterating the logic given every 30 ms.
 * **/

public class AnimationLogicListener {
    protected View mView;
    protected ViewGroup mViewParent;
    private Callable<Boolean> mLogic;
    protected Activity mActivity;

    private boolean isRunning = false;

    private Runnable mActionEnd;
    private Runnable mActionStart;

    protected ExtendedAnimatorListener mListenerIn;
    protected ExtendedAnimatorListener mListenerOut;

    protected ObjectAnimator mTransIn;
    protected ObjectAnimator mTransOut;

    protected Handler mHandler;

    public AnimationLogicListener(View view, ObjectAnimator transIn, ObjectAnimator transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mTransIn.setTarget(mView);
        mTransOut.setTarget(mView);

        mActivity = (Activity) mView.getContext();

        mActionEnd = () -> {return;};
        mActionStart = () -> {return;};

        mListenerIn = new ExtendedAnimatorListener(mActionStart,null);
        mTransIn.addListener(mListenerIn);
        mListenerOut = new ExtendedAnimatorListener(null,mActionEnd);
        mTransOut.addListener(mListenerOut);

        mHandler = new Handler();
    }

    public void setAnimationIn(AnimatorSet animationIn){
        mTransIn = animationIn;
    }

    public void setAnimationOut(AnimatorSet animationOut){
        mTransOut = animationOut;
    }

    public void setActionStart(Runnable actionStart){
        mActionStart = actionStart;
    }

    public void setActionEnd(Runnable actionEnd){
        mActionEnd = mActionEnd;
    }

    public View getView(){
        return mView;
    }

    public void setLogic(Callable<Boolean> logic) {
        mLogic = logic;
        startLogicLoop();
    }

    public void checkLogic() throws Exception {
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null && !mTransIn.isRunning()) {
                mTransIn.start();
            }
         } else {
            if(!mTransOut.isRunning()) {
                mTransOut.start();
            }
        }
    }

    public Callable<Boolean> getLogic(){
        return mLogic;
    }

    public void stop(){
        isRunning = false;
    }

    public void start(){
        isRunning = true;
        startLogicLoop();
    }

    public void startLogicLoop(){
        int delay = 62;
        isRunning = true;

        mHandler.postDelayed(new Runnable(){
            public void run(){
                try {
                    checkLogic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(isRunning && !mActivity.isFinishing())
                    mHandler.postDelayed(this, delay);
            }
        }, delay);
    }
}