package org.schulcloud.mobile.data.model.requestBodies;

import org.schulcloud.mobile.data.model.Course;

public class AddHomeworkRequest
{
    String schoolId;
    String teacherId;
    String name;
    Course courseId;
    Boolean isPrivate;
    String description;
    String availableDate;
    String dueDate;
    Boolean publicSubmissions;

    public AddHomeworkRequest(String schoolId, String teacherId, String name,
                              Course courseId, Boolean isPrivate,
                              String description, String availableDate,
                              String dueDate, Boolean publicSubmissions)
    {
        this.schoolId = schoolId;
        this.teacherId = teacherId;
        this.name = name;
        this.courseId = courseId;
        this.isPrivate = isPrivate;
        this.description = description;
        this.availableDate = availableDate;
        this.dueDate = dueDate;
        this.publicSubmissions = publicSubmissions;
    }
}
