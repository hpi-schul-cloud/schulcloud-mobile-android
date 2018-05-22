package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
 * new AnimationLogicListener(fooView,fooAnimationIn,fooAnimationOut).setLogic(() -> (bar==1)?true:false);
 *
 * Upon setting it, it will start iterating the logic given every 30 ms.
 * **/

public class AnimationLogicListener {
    private View mView;
    private ViewGroup mViewParent;
    private Callable<Boolean> mLogic;
    private ExtendedAnimatorListener mListener;
    private Activity mActivity;

    private boolean isRunning = false;

    private Animator mTransIn;
    private Animator mTransOut;

    private ObjectAnimator mTransInAnimator = new ObjectAnimator();

    private Handler mHandler;

    public AnimationLogicListener(View view, Animator transIn, Animator transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mTransIn.setTarget(mView);
        mTransOut.setTarget(mView);

        mActivity = (Activity) mView.getContext();

        mListener = new ExtendedAnimatorListener(null,null,null,null);

        mHandler = new Handler();
    }

    public void setAnimationIn(Animator animationIn){
        mTransIn = animationIn;
    }

    public void setAnimationOut(Animator animationOut){
        mTransOut = animationOut;
    }

    public void setAnimatorListener(ExtendedAnimatorListener listener){
        mListener = listener;
    }

    public View getView(){
        return mView;
    }

    public ExtendedAnimatorListener getListener() {
        return mListener;
    }

    public void setLogic(Callable<Boolean> logic) {
        mLogic = logic;
        if(!isRunning)
            startLogicLoop();
    }

    public void checkLogic() throws Exception {
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null && mTransIn.isRunning()) {
                mTransIn.start();
            }
        } else {
            if(mTransOut.isRunning()) {
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
