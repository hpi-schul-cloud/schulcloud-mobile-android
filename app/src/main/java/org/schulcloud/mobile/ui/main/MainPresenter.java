package org.schulcloud.mobile.ui.main;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;

import java.util.Stack;

import javax.inject.Inject;

@ConfigPersistent
public class MainPresenter extends BasePresenter<MainMvpView> {
    private static final int TAB_LEVEL_TOP = 0;
    private static final int TAB_LEVEL_LAST = -1;
    private static final int TAB_LEVEL_ONE_BACK = -2;

    private Stack<MainFragment>[] mFragments;
    private MainFragment mCurrentFragment;
    private int mCurrentTabIndex;
    private int mCurrentLevel;

    @Inject
    public MainPresenter() {}

    public void initTabs(@NonNull MainFragment[] topLevelFragments) {
        //noinspection unchecked
        mFragments = (Stack<MainFragment>[]) new Stack[topLevelFragments.length];
        for (int i = 0; i < topLevelFragments.length; i++) {
            Stack<MainFragment> stack = new Stack<>();
            stack.push(topLevelFragments[i]);
            mFragments[i] = stack;
        }

        showFragment(0, TAB_LEVEL_TOP);
    }

    public void onTabSelected(int tabIndex) {
        if (tabIndex != mCurrentTabIndex)
            showFragment(tabIndex, TAB_LEVEL_LAST);
        else // If the user selects the same tab again, navigate back to the first fragment of the stack
            showFragment(tabIndex, TAB_LEVEL_TOP);
    }
    /**
     * Adds the fragment as the child of parent, and shows it. If parent already has a child (and
     * possibly sub-children), those are removed.
     *
     * @param parent The parent of the added fragment
     * @param child  The fragment to add
     */
    public void addFragment(@NonNull MainFragment parent, @NonNull MainFragment child) {
        for (int tabIndex = 0; tabIndex < mFragments.length; tabIndex++) {
            Stack<MainFragment> tabStack = mFragments[tabIndex];
            for (int level = 0; level < tabStack.size(); level++) {
                MainFragment fragment = tabStack.get(level);
                if (parent.equals(fragment)) {
                    int diff = tabStack.size() - tabStack.indexOf(parent) - 1;
                    for (int k = 0; k < diff; k++)
                        tabStack.pop();
                    tabStack.push(child);
                    showFragment(tabIndex, TAB_LEVEL_LAST);
                    return;
                }
            }
        }
    }
    private void showFragment(int tabIndex, int level) {
        Stack<MainFragment> tabStack = mFragments[tabIndex];
        if (level == TAB_LEVEL_LAST)
            level = tabStack.size() - 1;
        else if (level == TAB_LEVEL_ONE_BACK)
            level = tabStack.size() - 2;

        if (level < 0)
            getMvpView().finish();
        else {
            while (tabStack.size() > level + 1)
                tabStack.pop();
            MainFragment fragment = tabStack.get(level);

            getMvpView().showFragment(fragment, mCurrentTabIndex, tabIndex);

            mCurrentFragment = fragment;
            mCurrentTabIndex = tabIndex;
            mCurrentLevel = level;
        }
    }
    public void onBackPressed() {
        if (!mCurrentFragment.onBackPressed())
            showFragment(mCurrentTabIndex, TAB_LEVEL_ONE_BACK);
    }
}

