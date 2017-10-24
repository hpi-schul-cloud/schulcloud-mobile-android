package org.schulcloud.mobile.ui.main;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.MenuItem;

import org.schulcloud.mobile.R;

import java.util.Stack;

/**
 * This class manages a {@link BottomNavigationView} inside an activity and all required fragment
 * transactions. It has to be initialized with {@link #init(int[], MainFragment[])}, and
 * child-fragments can be added via {@link #addFragment(MainFragment, MainFragment)}.
 */
public final class TabManager {
    private static final int TAB_LEVEL_TOP = 0;
    private static final int TAB_LEVEL_LAST = -1;

    private FragmentManager mFragmentManager;
    @IdRes
    private int mContainerId;
    private BottomNavigationView mBottomNavigationView;

    private SparseArray<Stack<MainFragment>> mFragments;
    private MainFragment mCurrentFragment;
    private int mCurrentTabIndex;
    private int mCurrentLevel;

    /**
     * Instantiates a new TabManager.
     *
     * @param mainActivity         The activity in which this TabManager is placed
     * @param containerId          Id of the view in which the fragments are positioned
     * @param bottomNavigationView The BottomNavigationView for interacting with the user
     */
    public TabManager(@NonNull MainActivity mainActivity, @IdRes int containerId,
            @NonNull BottomNavigationView bottomNavigationView) {
        mFragmentManager = mainActivity.getSupportFragmentManager();
        mContainerId = containerId;
        mBottomNavigationView = bottomNavigationView;
        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        showFragment(mFragments.indexOfKey(item.getItemId()), TAB_LEVEL_LAST);
                        return true;
                    }
                });
        mBottomNavigationView.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        showFragment(mFragments.indexOfKey(item.getItemId()), TAB_LEVEL_TOP);
                    }
                });

        mFragments = new SparseArray<>();
    }
    /**
     * Initializes the TabManager with the specified top level fragments. tabIds[i] and
     * topLevelFragments[i] belong together.
     *
     * @param tabIds            The ids of the menu items used in the {@link BottomNavigationView}
     * @param topLevelFragments The corresponding top level fragments
     */
    public void init(@NonNull @IdRes int[] tabIds, @NonNull MainFragment[] topLevelFragments) {
        if (tabIds.length != topLevelFragments.length)
            throw new IllegalArgumentException(
                    "tabIds and topLevelFragments must be of equal length");

        for (int i = 0; i < tabIds.length; i++) {
            Stack<MainFragment> stack = new Stack<>();
            stack.push(topLevelFragments[i]);
            mFragments.put(tabIds[i], stack);
        }

//        showFragment(0, TAB_LEVEL_TOP);
    }

    /**
     * Adds the fragment as the child of parent, and shows it. If parent already has a child (and
     * possibly sub-children), those are removed.
     *
     * @param parent The parent of the added fragment
     * @param child  The fragment to add
     */
    public void addFragment(@NonNull MainFragment parent, @NonNull MainFragment child) {
        for (int tabIndex = 0; tabIndex < mFragments.size(); tabIndex++) {
            Stack<MainFragment> tabStack = mFragments.valueAt(tabIndex);
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
        Stack<MainFragment> tabStack = mFragments.valueAt(tabIndex);
        if (level == TAB_LEVEL_LAST)
            level = tabStack.size() - 1;
        while (tabStack.size() > level + 1)
            tabStack.pop();
        MainFragment fragment = tabStack.get(level);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (tabIndex > mCurrentTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        else if (tabIndex < mCurrentTabIndex)
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(mContainerId, fragment);
        transaction.commit();

        mCurrentFragment = fragment;
        mCurrentTabIndex = tabIndex;
        mCurrentLevel = level;
    }

    /**
     * @return The currently displayed fragment
     */
    public MainFragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
