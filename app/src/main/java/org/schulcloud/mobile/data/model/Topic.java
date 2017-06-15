package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Topic extends RealmObject {
    public String _id;
    public String name;
    public String description;
    public String date;
    public String time;
    public String courseId;
    public Boolean hidden;
    public RealmList<Contents> contents;
}
