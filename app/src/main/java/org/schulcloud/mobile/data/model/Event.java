package org.schulcloud.mobile.data.model;


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.DirectoryRealmProxy;
import io.realm.EventRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { EventRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Event.class })
public class Event extends RealmObject {
    public String _id;
    public String type;
    public String title;
    public Boolean allDay;
    public String start;
    public String end;
    public String summary;
    public String location;

    @SerializedName("x-sc-courseId")
    public String xScCourseId;

    @SerializedName("x-sc-courseTimeId")
    public String xScCourseTimeId;
}
