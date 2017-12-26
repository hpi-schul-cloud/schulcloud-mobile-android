package org.schulcloud.mobile.util;

import android.app.Dialog;
import android.support.annotation.Nullable;

public final class DialogUtil {
    public static void cancel(@Nullable Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }
}
