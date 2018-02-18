package org.schulcloud.mobile.data.model;

import io.realm.RealmObject;

public class Contents extends RealmObject {
    public static final String COMPONENT_TEXT = "text";
    public static final String COMPONENT_RESOURCES = "resources";
    public static final String COMPONENT_GEOGEBRA = "geoGebra";
    public static final String COMPONENT_ETHERPAD = "Etherpad";

    public String component;
    public String title;
    public Boolean hidden;
    public Content content;
}