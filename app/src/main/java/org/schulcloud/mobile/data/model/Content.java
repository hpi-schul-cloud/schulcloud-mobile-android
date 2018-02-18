package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Content extends RealmObject{
    // text
    public String text;

    // resources
    public RealmList<Resource> resources;

    // geogebra
    public String materialId;

    // etherpad
    public String title;
    public String description;
    public String url;
}
