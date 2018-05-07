package org.schulcloud.mobile.ui.animation;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import java.util.concurrent.Callable;

public class AnimationLogicListener {
    View mView;
    ViewGroup mViewParent;
    Callable<Boolean> mLogic;
    Runnable mActionAdd;
    Runnable mActionRemove;

    Animation mTransIn;
    Animation mTransOut;

    public AnimationLogicListener(View view, Animation transIn, Animation transOut) {
        mView = view;
        mViewParent = (ViewGroup) view.getParent();
        mTransIn = transIn;
        mTransOut = transOut;

        mActionAdd = () -> mViewParent.addView(view);
        mActionRemove = () -> mViewParent.removeView(view);

    }

    public void setLogic(Callable<Boolean> logic) {
        mLogic = logic;
    }

    public void setActionAdd(Runnable actionAdd) {
        mActionAdd = actionAdd;
    }

    public void setActionRemove(Runnable actionRemove) {
        mActionRemove = actionRemove;
    }

    public void checkLogic() throws Exception {
        mView.clearAnimation();
        if (mLogic.call()) {
            if (mViewParent.findViewById(mView.getId()) == null) {
                mActionAdd.run();
                mView.startAnimation(mTransIn);
            }
        } else {
            mView.startAnimation(mTransOut);
            new Handler().postDelayed(mActionRemove, mTransIn.getDuration());
        }
    }
}
