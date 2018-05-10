package org.schulcloud.mobile.data.model;

import android.support.annotation.NonNull;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class User implements RealmModel {

    @PrimaryKey
    public String _id;
    public String firstName;
    public String lastName;
    public String displayName;
    public String email;
    public String schoolId;

    @NonNull
    public static User from(@NonNull CurrentUser currentUser) {
        User user = new User();
        user._id = currentUser._id;
        user.firstName = currentUser.firstName;
        user.lastName = currentUser.lastName;
        user.email = currentUser.email;
        user.schoolId = currentUser.schoolId;
        user.displayName = currentUser.displayName;
        return user;
    }

    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolId() {
        return schoolId;
    }
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}