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
    protected AnimationClass mTransIn, mTransOut;
    protected Runnable mActionIn, mActionOut;
    protected Handler mHandler;
    protected boolean running;

    public AnimationLogicListener(View view, Animation transIn, Animation transOut){
        mView = view;
        mViewParent = (ViewGroup) mView.getParent();

        mHandler = new Handler();

        mTransIn = new AnimationClass(transIn,mView);
        mTransOut = new AnimationClass(transOut,mView);
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
        mActionIn = actionIn;
    }

    public void setActionOut(Runnable actionOut){
        mActionOut = actionOut;
    }

    public Runnable getActionIn(){
        return mActionIn;
    }

    public Runnable getActionOut(){
        return mActionOut;
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
}
