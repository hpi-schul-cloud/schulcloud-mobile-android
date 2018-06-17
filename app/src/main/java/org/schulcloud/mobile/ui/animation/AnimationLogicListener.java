package org.schulcloud.mobile.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import org.schulcloud.mobile.injection.component.ApplicationComponent;

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
 * Upon given Logic, the listener will iterate the logic every 16 ms.
 * **/

public class AnimationLogicListener {
    protected View mView;
    protected ViewGroup mViewParent;
    private Callable<Boolean> mLogic;
    protected Activity mActivity;

    private boolean isRunning = false;

    protected WaiterObject mTaskIn;
    protected WaiterObject mTaskOut;

    protected ObjectAnimator mTransIn;
    protected ObjectAnimator mTransOut;

    protected Runnable mActionStart;
    protected Runnable mActionEnd;

    protected Handler mHandler;
    protected listenerAdapter listener;

    public AnimationLogicListener(View view, ObjectAnimator transIn, ObjectAnimator transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mTransIn.setTarget(mView);
        mTransOut.setTarget(mView);

        mActivity = (Activity) mView.getContext();
        listener = new listenerAdapter();

        mTaskIn = new WaiterObject();
        mTaskOut = new WaiterObject();
        mTransOut.addListener(listener);

        mHandler = new Handler();
    }

    public void setActionStart(Runnable actionStart) {
        mActionStart = actionStart;
    }

    public void setActionEnd(Runnable actionEnd) {
        mActionEnd = actionEnd;
    }

    public View getView() {
        return mView;
    }

    public void setLogic(Callable<Boolean> logic) {
        mLogic = logic;
        start();
    }

    public void checkLogic() throws Exception {
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null) {
                if (!mTransIn.isRunning() && !mTaskIn.wasStarted) {
                    mActionStart.run();
                    mTransIn.start();
                    mTaskIn.wasStarted = true;
                }
            }
        } else {
            if (!mTransOut.isRunning()) {
                if (!mTaskOut.wasStarted && mTaskIn.wasStarted) {
                    mTransOut.start();
                    mTaskOut.wasStarted = true;
                }
            }
        }
    }

    public Callable<Boolean> getLogic() {
        return mLogic;
    }

    public void stop() {
        isRunning = false;
    }

    public void start() {
        isRunning = true;
        startLogicLoop();
    }

    public void startLogicLoop() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    checkLogic();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isRunning)
                    mHandler.postDelayed(this, 16);
            }
        });
    }

    public class listenerAdapter extends AnimatorListenerAdapter{

        @Override
        public void onAnimationEnd(Animator animator){
            mActionEnd.run();
            mTaskOut.wasStarted = false;
            mTaskIn.wasStarted = false;
        }
    }
}