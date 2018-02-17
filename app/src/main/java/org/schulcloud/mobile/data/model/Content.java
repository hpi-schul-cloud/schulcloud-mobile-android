package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Content extends RealmObject{
    public String text;
    public RealmList<Resource> resources;
}
