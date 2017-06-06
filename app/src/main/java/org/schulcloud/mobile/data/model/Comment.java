package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Comment implements RealmModel {
    @PrimaryKey
    public String _id;
    public String comment;
    public String submissionId;
    public String author;
    public String createdAt;
}
