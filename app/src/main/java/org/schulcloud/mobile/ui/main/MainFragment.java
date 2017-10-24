package org.schulcloud.mobile.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;

import org.schulcloud.mobile.ui.base.BaseFragment;

/**
 * Base class for any fragment that will be shown inside {@link MainActivity}.
 */
public abstract class MainFragment extends BaseFragment {
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity)
            mMainActivity = (MainActivity) context;
        else
            throw new IllegalStateException(MainFragment.class.getName()
                    + " may only be attached to an instance of " + MainActivity.class.getName());
    }

    /**
     * Return the {@link MainActivity} this fragment is associated with. May return {@code null} if
     * the fragment has not yet been associated with it's activity.
     */
    @NonNull
    public final MainActivity getMainActivity() {
        return mMainActivity;
    }
}
