package org.schulcloud.mobile.data.model;

import io.realm.RealmObject;

public class Contents extends RealmObject {
    public String component;
    public String title;
    public Boolean hidden;
    public Content content;
}