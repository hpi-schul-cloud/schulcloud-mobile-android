package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Required for avoiding population of the whole course object
 */
@RealmClass
public class CourseHomework implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String name;
    public String description;
    public String color;
    public RealmList<RealmString> substitutionIds;
    public RealmList<RealmString> userIds;
}
