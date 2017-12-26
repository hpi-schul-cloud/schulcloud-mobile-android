package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;

import java.io.File;

public final class PathUtil {
    @NonNull
    public static String trimLeadingSlash(@NonNull String path) {
        if (path.length() > 0 && path.charAt(0) == File.separatorChar)
            return path.substring(1);
        return path;
    }
    @NonNull
    public static String trimTrailingSlash(@NonNull String path) {
        if (path.length() > 1 && path.charAt(path.length() - 1) == File.separatorChar)
            return path.substring(0, path.length() - 1);
        return path;
    }
    @NonNull
    public static String trimSlashes(@NonNull String path) {
        return trimTrailingSlash(trimLeadingSlash(path));
    }

    @NonNull
    public static String[] getAllParts(@NonNull String path) {
        return path.split(File.separator);
    }

    @NonNull
    public static String parent(@NonNull String path) {
        return path.substring(0, trimTrailingSlash(path).lastIndexOf(java.io.File.separator));
    }
}
