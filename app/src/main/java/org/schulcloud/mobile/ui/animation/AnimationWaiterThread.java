package org.schulcloud.mobile.ui.animation;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnimationWaiterThread extends Thread{
    public static WaitHandler mHandler;

    @Inject
    public AnimationWaiterThread(){
        mHandler = new WaitHandler();
    }

    public void run(){
        Looper.prepare();

        mHandler = new WaitHandler();

        Looper.loop();
    }

    public void wait(WaiterObject waiterObject){
        Message message = new Message();
        message.obj = waiterObject;
        mHandler.sendMessage(message);
    }

    public class WaitHandler extends Handler{

        @Override
        public void handleMessage(Message msg){
            WaiterObject waiterObject = (WaiterObject) msg.obj;
            waiterObject.wasStarted = true;
            postDelayed(() -> waiterObject.isFinished = true,waiterObject.mWaitingTime);
        }
    }
}
