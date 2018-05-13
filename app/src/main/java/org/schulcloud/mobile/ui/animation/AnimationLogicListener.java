package org.schulcloud.mobile.ui.animation;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 * This class is for using animations, which will be triggered under certain circumstances.
 * To do this you give the class code in form of a Callable, which returns true or false.
 * I.e. () -> (value == 1)?true:false
 * The class will then listen.
 *
 * What animations play you can set via
 * **/

public class AnimationLogicListener {
    View mView;
    ViewGroup mViewParent;
    Callable<Boolean> mLogic;
    Runnable mActionAdd;
    Runnable mActionRemove;

    Animation mTransIn;
    Animation mTransOut;
    boolean mTransOutRunning;
    boolean mTransInRunning;
    ObjectAnimator mTransInAnimator = new ObjectAnimator();

    Handler mHandler;

    public AnimationLogicListener(View view, Animation transIn, Animation transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;
        mTransInRunning = false;
        mTransOutRunning = false;


        mActionAdd = () -> {mViewParent.addView(view); mView.clearAnimation();};
        mActionRemove = () -> { mTransOutRunning = false;mViewParent.removeView(view);};

        mHandler = new Handler();

        startLogicLoop();
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
    }

    public void setActionAdd(Runnable actionAdd) {
        mActionAdd = () -> {actionAdd.run(); mView.clearAnimation();};
    }

    public void setActionRemove(Runnable actionRemove) {
        mActionRemove = () -> {actionRemove.run();mTransOutRunning = false; mView.clearAnimation();};
    }

    public void checkLogic() throws Exception {
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null && !mTransInRunning) {
                mActionAdd.run();
                mTransInRunning = true;
                mView.startAnimation(mTransIn);
                mHandler.postDelayed(() -> {mTransInRunning = false;}, mTransIn.getDuration()*2);
            }
        } else {
            if(!mTransOutRunning) {
                mTransOutRunning = true;
                mView.startAnimation(mTransOut);
                mHandler.postDelayed(mActionRemove, mTransOut.getDuration());
            }
        }
    }

    public void startLogicLoop(){
        int delay = 62;

        mHandler.postDelayed(new Runnable(){
            public void run(){
                try {
                    checkLogic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(this, delay);
            }
        }, delay);
    }
}
