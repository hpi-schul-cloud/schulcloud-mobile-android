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
    public String studentId;
    public String comment;
    public Integer grade;
    public RealmList<Comment> comments;
    public String homeworkId;
    public String gradeComment;
    public String createdAt;
}
