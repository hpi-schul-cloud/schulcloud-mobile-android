package org.schulcloud.mobile.ui.animation;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class LogicListenerThread{
    private Handler mHandler;
    private boolean isRunning = false;

    private static List<AnimationLogicListener> mListeners;

    public LogicListenerThread(){
        mHandler = new Handler();
        mListeners = new ArrayList<AnimationLogicListener>();
        startLoop();
    }

    public static void addListener(AnimationLogicListener listener){
        mListeners.add(listener);
    }

    public static AnimationLogicListener getListener(int index){
        return mListeners.get(index);
    }

    public static void removeListener(AnimationLogicListener listener){
        mListeners.remove(listener);
    }

    public static AnimationLogicListener getListener(AnimationLogicListener listener){
        return mListeners.get(mListeners.indexOf(listener));
    }

    public void startLoop(){
        isRunning = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < mListeners.size(); i++){
                    try {
                        mListeners.get(i).checkLogic();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(isRunning)
                    mHandler.postDelayed(this,16);
            }
        });
    }

}
