package org.schulcloud.mobile.ui.animation;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LogicListenerThread extends Thread {
    private Handler mHandler;
    private boolean isRunning = false;

    private List<AnimationLogicListener> mListeners;

    @Inject
    public LogicListenerThread(){
        run();
    }

    public void addListener(AnimationLogicListener listener){
        mListeners.add(listener);
    }

    public AnimationLogicListener getListener(int index){
        return mListeners.get(index);
    }

    public void removeListener(AnimationLogicListener listener){
        mListeners.remove(listener);
    }

    public void run(){
        if(!isRunning) {
            isRunning = true;
            Looper.prepare();

            mHandler = new Handler() {
                public void handleLogics() throws Exception {
                    for (int i = 0; i < mListeners.size(); i++) {
                        mListeners.get(i).checkLogic();
                    }
                }
            };

            Looper.loop();
        }
    }
}
