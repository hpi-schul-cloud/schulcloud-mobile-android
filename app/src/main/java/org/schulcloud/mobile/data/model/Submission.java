package org.schulcloud.mobile.data.model;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Submission implements RealmModel {
    @PrimaryKey
    public String _id;
    public String schoolId;
    public String homeworkId;
    public String studentId;
    public String comment;
    public String createdAt;

    public Integer grade;
    public String gradeComment;

    public RealmList<Comment> comments;
}
