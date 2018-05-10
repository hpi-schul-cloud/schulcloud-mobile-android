package org.schulcloud.mobile.data.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Homework implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String teacherId;
    public String name;
    public String description;
    public String availableDate;
    public String dueDate;
    public CourseHomework courseId;

    @SerializedName("private")
    public Boolean restricted;
    public Boolean publicSubmissions;

    public boolean isPrivate() {
        return restricted == null || restricted;
    }
    public boolean hasPrivateSubmissions() {
        return publicSubmissions == null || publicSubmissions;
    }
}
