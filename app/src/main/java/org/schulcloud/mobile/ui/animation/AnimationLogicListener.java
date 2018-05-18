package org.schulcloud.mobile.ui.animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import java.util.Timer;
import java.util.TimerTask;
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
    private Runnable mActionAdd;
    private Runnable mActionRemove;
    private Activity mActivity;

    private boolean isRunning = false;

    private Animation mTransIn;
    private Animation mTransOut;

    private ObjectAnimator mTransInAnimator = new ObjectAnimator();

    private Handler mHandler;

    public AnimationLogicListener(View view, Animation transIn, Animation transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mActivity = (Activity) mView.getContext();

        mActionAdd = () -> {mViewParent.addView(view); mView.clearAnimation();};
        mActionRemove = () -> {mViewParent.removeView(view);};

        mHandler = new Handler();
    }

    public void setAnimationIn(Animation animationIn){
        mTransIn = animationIn;
    }

    public void setAnimationOut(Animation animationOut){
        mTransOut = animationOut;
    }

    public View getView(){
        return mView;
    }

    public void setLogic(Callable<Boolean> logic) {
        mLogic = logic;
        if(!isRunning)
            startLogicLoop();
    }

    public void setActionAdd(Runnable actionAdd) {
        mActionAdd = () -> {actionAdd.run(); mView.clearAnimation();};
    }

    public void setActionRemove(Runnable actionRemove) {
        mActionRemove = () -> {actionRemove.run(); mView.clearAnimation();};
    }

    public void checkLogic() throws Exception {
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null && mView.hasTransientState()) {
                mActionAdd.run();
                mView.startAnimation(mTransIn);
                mHandler.postDelayed(() -> mView.setHasTransientState(false),mTransIn.getDuration());
            }
        } else {
            if(!mView.hasTransientState()) {
                mView.startAnimation(mTransOut);
                mHandler.postDelayed(() -> {mActionRemove.run(); mView.setHasTransientState(false);}, mTransOut.getDuration());
            }
        }
    }

    public Callable<Boolean> getLogic(){
        return mLogic;
    }

    public Runnable getActionAdd(){
        return mActionAdd;
    }

    public Runnable getActionRemove(){
        return mActionRemove;
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
