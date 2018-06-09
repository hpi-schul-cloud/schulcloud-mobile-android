package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

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

    protected ExtendedAnimatorListener mListenerIn;
    protected ExtendedAnimatorListener mListenerOut;

    protected ObjectAnimator mTransIn;
    protected ObjectAnimator mTransOut;

    protected Handler mHandler;
    LogicListenerThread listenerThread;

    public AnimationLogicListener(View view, ObjectAnimator transIn, ObjectAnimator transOut) {
        listenerThread = new LogicListenerThread();
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mTransIn.setTarget(mView);
        mTransOut.setTarget(mView);

        mActivity = (Activity) mView.getContext();

        mListenerIn = new ExtendedAnimatorListener(mTransIn,null,null);
        mListenerOut = new ExtendedAnimatorListener(mTransOut,null,null);

        mHandler = new Handler();
        LogicListenerThread.addListener(this);
    }

    public void setActionStart(Runnable actionStart){
        mListenerIn.mActionStart = actionStart;
    }

    public void setActionEnd(Runnable actionEnd){
        mListenerOut.mActionEnd = actionEnd;
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
            if(mViewParent.findViewById(mView.getId()) == null) {
                if (!mTransIn.isRunning()) {
                    mTransIn.start();
                }
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
        listenerThread.removeListener(this);
    }

    public void start(){
        isRunning = true;
        startLogicLoop();
    }

    public void startLogicLoop(){
        listenerThread.addListener(this);
    }

    private class ExtendedAnimatorListener extends AnimatorListenerAdapter {
        private Runnable mActionStart;
        private Runnable mActionEnd;
        private Handler mHandler;
        private int listenDelay = 16;
        private ObjectAnimator mAnimator;

        public ExtendedAnimatorListener(ObjectAnimator animator, Runnable actionStart, Runnable actionEnd){
            mAnimator = animator;
            mActionStart = actionStart == null?() -> {return;}:actionStart;
            mActionEnd = actionEnd == null?() -> {return;}:actionEnd;
            mHandler = new Handler();
            mAnimator.addListener(this);
        }

        @Override
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
            mActionStart.run();
            mView.clearAnimation();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if(mAnimator.getCurrentPlayTime() == mAnimator.getDuration())
                mActionEnd.run();
            mView.clearAnimation();
        }
    }
}