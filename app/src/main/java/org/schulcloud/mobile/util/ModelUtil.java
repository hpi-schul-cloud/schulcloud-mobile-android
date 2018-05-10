package org.schulcloud.mobile.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.schulcloud.mobile.R;
import org.schulcloud.mobile.data.model.User;

/**
 * Date: 5/10/2018
 */
public final class ModelUtil {
    private ModelUtil() { }

    @Nullable
    public static String getUserName(@NonNull Context context, @Nullable User user) {
        if (user == null)
            return null;

        if (!TextUtils.isEmpty(user.displayName))
            return user.displayName;

        return context.getString(R.string.general_user_name,
                user.firstName, user.lastName);
    }
}
