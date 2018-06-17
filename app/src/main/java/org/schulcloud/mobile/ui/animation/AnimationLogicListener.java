package org.schulcloud.mobile.ui.animation;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.concurrent.Callable;

public class AnimationLogicListener {

    protected Callable<Boolean> mLogic;
    protected View mView;
    protected ViewGroup mViewParent;
    protected Animation mTransIn, mTransOut;
    protected AnimListener animListenerIn, animListenerOut;
    protected Handler mHandler;
    protected boolean running;

    public AnimationLogicListener(View view, Animation transIn, Animation transOut){
        mView = view;
        mViewParent = (ViewGroup) mView.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        animListenerIn = new AnimListener();
        animListenerOut =  new AnimListener();

        animListenerIn.mActionIn = () -> {return;};
        animListenerOut.mActionIn = () -> {return;};

        mHandler = new Handler();
    }

    public void start(){
        running = true;
        startLogicLoop();
    }

    public void stop(){
        running = false;
    }

    public void setLogic(Callable<Boolean> logic){
        mLogic = logic;
        start();
    }

    public Callable<Boolean> getLogic(){
        return mLogic;
    }

    public void setActionIn(Runnable actionIn){
        animListenerIn.mActionIn = actionIn;
    }

    public void setActionOut(Runnable actionOut){
        animListenerOut.mActionOut = actionOut;
    }

    public Runnable getActionIn(){
        return animListenerIn.mActionIn;
    }

    public Runnable getActionOut(){
        return animListenerOut.mActionOut;
    }

    public void startLogicLoop(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    checkLogic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(running)
                    mHandler.postDelayed(this,16);
            }
        });
    }


    /*** Override this method! ***/
    public void checkLogic() throws Exception {
        return;
    }

    public class AnimListener implements Animation.AnimationListener {
        public Runnable mActionIn;
        public Runnable mActionOut;
        public InfObject mInfo;

        @Override
        public void onAnimationStart(Animation animation) {
            mActionIn.run();
            mInfo.wasStarted = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mActionOut.run();
            mInfo.isFinished = true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            return;
        }
    }
}
