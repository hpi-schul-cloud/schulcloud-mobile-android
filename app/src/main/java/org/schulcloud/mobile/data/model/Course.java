package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Course implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String name;
    public String description;
    public String color;
    public RealmList<Times> times;
    public String startDate;
    public String untilDate;
    public Boolean gradeSystem;
    public RealmList<User> substitutionIds;
    public RealmList<User> teacherIds;
    public RealmList<User> userIds;
}
