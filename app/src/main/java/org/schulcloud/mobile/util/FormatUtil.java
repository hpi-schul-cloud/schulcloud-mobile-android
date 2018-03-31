package org.schulcloud.mobile.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date: 3/31/2018
 */
public final class FormatUtil {
    private static final String TAG = FormatUtil.class.getSimpleName();

    public static final SimpleDateFormat DATE_FORMAT_API =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
    public static final DateFormat DATE_FORMAT_USER = SimpleDateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);

    @Nullable
    public static Date parseDate(@Nullable String apiDate) {
        if (apiDate == null)
            return null;

        try {
            return DATE_FORMAT_API.parse(apiDate);
        } catch (ParseException e) {
            Log.w(TAG, "Error parsing date: " + apiDate);
            return null;
        }
    }
    @NonNull
    public static String toUserString(@Nullable Date date) {
        if (date == null)
            return "";

        return DATE_FORMAT_USER.format(date);
    }
    @NonNull
    public static String toApiString(@NonNull Date date) {
        return DATE_FORMAT_API.format(date);
    }
    @NonNull
    public static String apiToDate(@Nullable String apiDate) {
        return toUserString(parseDate(apiDate));
    }
}
