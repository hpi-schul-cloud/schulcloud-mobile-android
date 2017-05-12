package org.schulcloud.mobile.data.model;


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import io.realm.DirectoryRealmProxy;
import io.realm.RealmObject;

@Parcel(implementations = { EventRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Event.class })
public class Event extends RealmObject {
    private String _id;
    private String type;
    private String title;
    private Boolean allDay;
    private Integer start;
    private Integer end;
    private String summary;
    private String location;

    @SerializedName("x-sc-courseId")
    private String xScCourseId;

    @SerializedName("x-sc-courseTimeId")
    private String sScCourseTimeId;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getAllDay() {
        return allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
