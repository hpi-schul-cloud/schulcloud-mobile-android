package org.schulcloud.mobile.ui.main;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.ui.base.MvpView;
import org.schulcloud.mobile.util.NetworkUtil;
import org.schulcloud.mobile.util.RxUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class MainPresenter<V> extends BasePresenter<MainMvpView<V>> {
    private static final int TAB_LEVEL_TOP = 0;
    private static final int TAB_LEVEL_LAST = -1;
    private static final int TAB_LEVEL_ONE_BACK = -2;

    private final UserDataManager mUserDataManager;
    private Subscription mCurrentUserSubscription;

    private Uri mStartUrl = null;

    private Stack<Integer>[] mViewIds;
    private int mCurrentViewId;
    private int mCurrentTabIndex;
    private int mCurrentLevel;

    @Inject
    public MainPresenter(UserDataManager userDataManager) {
        mUserDataManager = userDataManager;
        mCurrentViewId = -1;

        sendToView(v -> {
            //noinspection unchecked
            mViewIds = (Stack<Integer>[]) new Stack[v.getTabCount()];
            for (int i = 0; i < mViewIds.length; i++)
                mViewIds[i] = new Stack<>();

            showView(0, TAB_LEVEL_TOP, null, false);
            if (mStartUrl != null)
                v.loadViewForUrl(mStartUrl);
        });
    }

    public void setStartUrl(@Nullable Uri startUrl) {
        mStartUrl = startUrl;
    }

    /**
     * Checks whether there is already a logged-in user, if not so go to sign-in screen
     */
    public void checkSignedIn(@NonNull Context context) {
        // 1. try to get currentUser from prefs
        String currentUserId = mUserDataManager.getCurrentUserId();

        // value is "null" as String if pref does not exist
        if (currentUserId.equals("null")) {
            sendToView(MvpView::goToSignIn);
            return;
        }

        // 2. if there is a valid jwt in the storage (just online)
        if (NetworkUtil.isNetworkConnected(context)) {
            RxUtil.unsubscribe(mCurrentUserSubscription);
            mCurrentUserSubscription = mUserDataManager.syncCurrentUser(currentUserId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            // onNext
                            currentUser -> {},
                            // onError, check failed
                            error -> {
                                Timber.e(error, "There was an error while fetching currentUser.");
                                sendToView(MvpView::goToSignIn);
                            });
        }
    }

    public void onTabSelected(int tabIndex) {
        if (tabIndex != mCurrentTabIndex)
            showView(tabIndex, TAB_LEVEL_LAST, null, false);
        else // If the user selects the same tab again, navigate back to the first view of the stack
            showView(tabIndex, TAB_LEVEL_TOP, null, false);
    }
    public void addView(int childId, @NonNull V child) {
        addView(getCurrentViewId(), childId, child);
    }
    /**
     * Adds the view as the child of parent, and shows it. If parent already has a child (and
     * possibly sub-children), those are removed.
     *
     * @param parentId The parent's ID of the view to add
     * @param childId  The ID of the view to add
     */
    public void addView(int parentId, int childId, @NonNull V child) {
        for (int tabIndex = 0; tabIndex < mViewIds.length; tabIndex++) {
            Stack<Integer> tabStack = mViewIds[tabIndex];
            int level = tabStack.indexOf(parentId);
            if (level < 0)
                continue;

            popTabStack(tabStack, level + 1);
            tabStack.push(childId);
            showView(tabIndex, TAB_LEVEL_LAST, child, false);
            break;
        }
    }
    public void navigateToView(int tabIndex, int level) {
        showView(tabIndex, level, null, false);
    }
    /**
     * Removes the specified view from the hierarchy. Any child views will be removed too.
     *
     * @param viewId The view to be removed.
     * @return True if the view was removed, false otherwise (e.g., the view is the top level view).
     */
    public boolean removeView(int viewId) {
        if (viewId == mCurrentViewId)
            return showView(mCurrentTabIndex, TAB_LEVEL_ONE_BACK, null, false);

        int i = mViewIds[mCurrentTabIndex].indexOf(viewId);
        if (i >= 0)
            return showView(mCurrentTabIndex, i, null, false);

        for (Stack<Integer> tabStack : mViewIds) {
            int level = tabStack.indexOf(viewId);
            if (level < 0)
                continue;

            popTabStack(tabStack, level);
            break;
        }
        return true;
    }
    /**
     * Displays the view identified by its tab index and level. Child views are removed
     * automatically.
     *
     * @param tabIndex     The index of the tab that the view belongs to.
     * @param level        The level of the view. {@link #TAB_LEVEL_TOP}, {@link #TAB_LEVEL_LAST}
     *                     and {@link #TAB_LEVEL_ONE_BACK} are allowed.
     * @param closeIfEmpty If set to true and the other parameters would lead to the top level view
     *                     being removed, the app will be closed. Handy for back navigation, but
     *                     not
     *                     if a view tries to remove itself.
     * @return True if the view identified by {@code tabIndex} and {@code level} is now displayed,
     * false otherwise (e.g., if that would have closed the app and that isn't permitted).
     */
    private boolean showView(int tabIndex, int level, @Nullable V newView, boolean closeIfEmpty) {
        Stack<Integer> tabStack = mViewIds[tabIndex];
        if (tabStack.isEmpty()) {
            // TODO: Think of a safe asynchronous way
            Pair<Integer, V> pair = getViewOrThrow().createInitialView(tabIndex);
            tabStack.push(pair.first);
            newView = pair.second;
            level = 0;
        } else {
            if (level == TAB_LEVEL_LAST)
                level = tabStack.size() - 1;
            else if (level == TAB_LEVEL_ONE_BACK)
                level = tabStack.size() - 2;

            if (level < 0) {
                if (!closeIfEmpty)
                    return false;
                sendToView(MainMvpView::finish);
                return true;
            }
        }

        popTabStack(tabStack, level + 1);

        final int oldViewId = mCurrentViewId;
        final int viewId = tabStack.get(level);
        final V newViewFinal = newView;

        mCurrentViewId = viewId;
        mCurrentTabIndex = tabIndex;
        mCurrentLevel = level;

        sendToView(v -> v.showView(oldViewId, viewId, newViewFinal, mCurrentTabIndex, tabIndex));
        return true;
    }
    private void popTabStack(@NonNull Stack<Integer> tabStack, int endSize) {
        if (endSize >= tabStack.size())
            return;

        List<Integer> viewIds = new LinkedList<>();
        while (tabStack.size() > endSize)
            viewIds.add(tabStack.pop());
        sendToView(v -> v.removeViews(viewIds));
    }

    public void onBackPressed() {
        if (!getViewOrThrow().currentViewHandlesBack(mCurrentViewId))
            showView(mCurrentTabIndex, TAB_LEVEL_ONE_BACK, null, true);
    }

    public int getCurrentViewId() {
        return mCurrentViewId;
    }
}

