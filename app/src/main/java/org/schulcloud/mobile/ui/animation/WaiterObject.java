package org.schulcloud.mobile.ui.animation;

public class WaiterObject {
    public boolean wasStarted = false;
    public boolean isFinished = true;
    public int mWaitingTime = 0;

    public WaiterObject(int waitingTime){
        mWaitingTime = waitingTime;
    }

    public void setTime(int time){
        mWaitingTime = time;
    }

    public int getTime(){
        return mWaitingTime;
    }
}