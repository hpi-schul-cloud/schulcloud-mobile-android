package org.schulcloud.mobile.ui.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.schulcloud.mobile.util.Action;
import org.schulcloud.mobile.util.SuperNotCalledException;
import org.schulcloud.mobile.data.datamanagers.CourseDataManager;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.FeedbackDataManager;
import org.schulcloud.mobile.data.datamanagers.FileDataManager;
import org.schulcloud.mobile.data.datamanagers.HomeworkDataManager;
import org.schulcloud.mobile.data.datamanagers.NewsDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.SubmissionDataManager;
import org.schulcloud.mobile.data.datamanagers.TopicDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the view that
 * can be accessed in the following two ways:
 * <ul>
 * <li>{@link #getViewOrThrow()}: Get the view synchronously. Suitable for instant callbacks</li>
 * <li>{@link #sendToView(Action)}: Send an action to the view as soon as it is available. Suitable
 * for reactive calls to the backend, for example.</li>
 * </ul>
 *
 * @see <a href="https://android.jlelse.eu/dont-put-view-null-checks-in-your-presenters-4b6026c67423">Inspiration
 * for the get-view pattern</a>
 */
public class BasePresenter<V extends MvpView> implements Presenter<V> {
    private static String TAG = BasePresenter.class.getCanonicalName();

    private boolean mCalled;
    private V mView;
    private BlockingQueue<Action<V>> mPostponedViewActions;

    public BasePresenter() {
        mCalled = true;
        mPostponedViewActions = new LinkedBlockingQueue<>();
    }

    @CallSuper
    protected void onViewAttached(@NonNull V view) {
        if (mCalled)
            throw new IllegalAccessError(
                    "Don't call #onViewAttached(V) directly, call #attachView(V)");
        mCalled = true;
    }
    @CallSuper
    protected void onViewDetached() {
        if (mCalled)
            throw new IllegalAccessError(
                    "Don't call #onViewDetached(V) directly, call #detachView(V)");
        mCalled = true;
    }

    @Override
    public final void attachView(@NonNull V view) {
        if (mView != null)
            if (mView == view)
                return;
            else
                throw new IllegalStateException(
                        "A view is already attached, call #detachView() first");
        mView = view;

        mCalled = false;
        onViewAttached(view);
        if (!mCalled)
            throw new SuperNotCalledException(
                    "Presenter " + this + " didn't call call through to super.onViewAttached(V)");
        mCalled = true;

        sendPostponedActionsToView(view);
    }
    @Override
    public final void detachView() {
        if (mView == null) {
            Log.v(TAG, "#detachView(): View is already detached");
            return;
        }
        mView = null;

        mCalled = false;
        onViewDetached();
        if (!mCalled)
            throw new SuperNotCalledException(
                    "Presenter " + this + " didn't call call through to super.onViewDetached()");
        mCalled = true;

        mView = null;
    }

    protected boolean isViewAttached() {
        return mView != null;
    }
    /**
     * Returns the view if it is currently attached, and throws an exception otherwise. Suitable for
     * an instant callback.
     */
    @NonNull
    protected V getViewOrThrow() {
        if (mView == null)
            throw new IllegalStateException(
                    "The view is currently not attached. Call #sendToView(ViewAction) instead");
        return mView;
    }
    /**
     * Executes the action if the view is currently attached. Otherwise it is postponed until the
     * view is attached again. Suitable for asynchronous callbacks
     *
     * @param action The action to send to the view. The parameter is the currently attached view.
     */
    protected void sendToView(final @NonNull Action<V> action) {
        if (mView != null)
            action.call(mView);
        else
            mPostponedViewActions.add(action);
    }
    private void sendPostponedActionsToView(final @NonNull V view) {
        while (!mPostponedViewActions.isEmpty())
            mPostponedViewActions.poll().call(view);
    }
}
