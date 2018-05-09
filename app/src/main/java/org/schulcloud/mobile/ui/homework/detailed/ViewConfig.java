package org.schulcloud.mobile.ui.homework.detailed;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.schulcloud.mobile.data.model.Homework;

/**
 * Date: 5/9/2018
 */
public class ViewConfig {
    @NonNull
    public final Homework homework;
    @NonNull
    public final String userId;
    @Nullable
    public final String studentId;

    public ViewConfig(@NonNull Homework homework, @NonNull String userId,
            @Nullable String studentId) {
        this.homework = homework;
        this.userId = userId;
        this.studentId = studentId;
    }
}
