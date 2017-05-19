package org.schulcloud.mobile.util;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;

import org.schulcloud.mobile.data.model.Event;

import java.util.HashSet;
import java.util.Set;

import static org.schulcloud.mobile.ui.settings.SettingsActivity.CALENDAR_PERMISSION_CALLBACK_ID;

public class CalendarContentUtil {
    public static final String[] FIELDS = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };

    public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");

    private ContentResolver contentResolver;
    private Set<String> calendars = new HashSet<String>();
    private Context context;

    public CalendarContentUtil(Context ctx) {
        this.context = ctx;
        this.contentResolver = ctx.getContentResolver();
    }

    public Set<String> getCalendars() {
        // Fetch a list of all calendars sync'd with the device and their display names
        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    // This is actually a better pattern:
                    String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
                    Boolean selected = !cursor.getString(3).equals("0");
                    calendars.add(displayName);
                }
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }

        return calendars;
    }

    public long createEvent(Integer calendarId, Event event) {
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, event.start);
        values.put(Events.DTEND, event.end);
        values.put(Events.TITLE, event.title);
        values.put(Events.DESCRIPTION, event.summary);
        values.put(Events.CALENDAR_ID, calendarId);
        values.put(Events.EVENT_TIMEZONE, "Germany/Berlin");
        values.put(Events.UID_2445, event._id);

        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PermissionsUtil.checkPermissions(CALENDAR_PERMISSION_CALLBACK_ID, (Activity) this.context, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
            return -1;
        }


        Uri uri = this.contentResolver.insert(Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        return Long.parseLong(uri.getLastPathSegment());
    }

    public Integer deleteEventByUid(String uid) {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PermissionsUtil.checkPermissions(CALENDAR_PERMISSION_CALLBACK_ID, (Activity) this.context, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
            return -1;
        }


        Uri uri = Events.CONTENT_URI;
        String whereQuery = "(" + Events.UID_2445 + " = \'" + uid + "\')";
        return this.contentResolver.delete(uri, whereQuery, null);
    }
}
