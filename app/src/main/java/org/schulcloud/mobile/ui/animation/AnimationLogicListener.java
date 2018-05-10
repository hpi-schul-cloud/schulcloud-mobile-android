package org.schulcloud.mobile.ui.animation;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation;

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
    ObjectAnimator mTransInAnimator = new ObjectAnimator();

    public AnimationLogicListener(View view, Animation transIn, Animation transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mActionAdd = () -> {mViewParent.addView(view); mView.clearAnimation();};
        mActionRemove = () -> {mViewParent.removeView(view); mView.clearAnimation();};

        new Handler().post(() -> {
            try {
                checkLogic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        mActionRemove = () -> {actionRemove.run(); mView.clearAnimation();};
    }

    public void checkLogic() throws Exception {
        mView.clearAnimation();
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null) {
                mActionAdd.run();
                mView.startAnimation(mTransIn);
                new Handler().postDelayed(() -> {mView.clearAnimation();},mTransIn.getDuration());
            }
        } else {
            if(!mTransOut.hasStarted()) {
                mView.startAnimation(mTransOut);
                new Handler().postDelayed(mActionRemove, mTransOut.getDuration());
            }
        }
    }
}
