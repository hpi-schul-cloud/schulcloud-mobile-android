package org.schulcloud.mobile.data.model;

import io.realm.RealmObject;

/**
 * Date: 2/17/2018
 */
public class Resource extends RealmObject {
    public String url;
    public String client;
    public String title;
    public String description;
}
