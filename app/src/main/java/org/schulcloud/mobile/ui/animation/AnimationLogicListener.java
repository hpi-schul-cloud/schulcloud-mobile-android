package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
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

    protected Runnable mActionIn;
    protected ExtendedAnimatorListener mListenerOut;

    protected ValueAnimator mTransIn;
    protected ValueAnimator mTransOut;

    protected Handler mHandler;

    public AnimationLogicListener(View view, ValueAnimator transIn, ValueAnimator transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mTransIn.setTarget(mView);
        mTransOut.setTarget(mView);

        mActivity = (Activity) mView.getContext();

        mActionIn = () -> {return;};

        mListenerOut = new ExtendedAnimatorListener(null,null);
        mTransOut.addListener(mListenerOut);

        mHandler = new Handler();
    }

    public void setActionStart(Runnable actionStart){
        mActionIn = actionStart;
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
                    mActionIn.run();
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

    private class ExtendedAnimatorListener extends AnimatorListenerAdapter {
        private Runnable mActionStart;
        private Runnable mActionEnd;
        private Handler mHandler;
        private int listenDelay = 16;

        public ExtendedAnimatorListener( Runnable actionStart, Runnable actionEnd){
            mActionStart = actionStart == null?() -> {return;}:actionStart;
            mActionEnd = actionEnd == null?() -> {return;}:actionEnd;
            mHandler = new Handler();
        }

        @Override
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
            mActionStart.run();
        }

        public void onAnimatorEnd() {
            //Debug.waitForDebugger();
            mActionEnd.run();
        }

        public void listenForEnd(){
            mHandler.postDelayed(() -> {
                if(mTransOut.getCurrentPlayTime() == mTransOut.getDuration()){
                    onAnimatorEnd();
                }
            },listenDelay);
        }
    }
}