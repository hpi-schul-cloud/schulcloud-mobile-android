package org.schulcloud.mobile.data.model;

import io.realm.RealmObject;

public class Content extends RealmObject {
    public String component;
    public String title;
    public Boolean hidden;
}