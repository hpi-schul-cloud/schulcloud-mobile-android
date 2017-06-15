package org.schulcloud.mobile.data.model;


import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Times implements RealmModel {
    @PrimaryKey
    public Integer weekday;
    public Integer startTime;
    public Integer duration;
    public String eventId;
    public String room;
}
