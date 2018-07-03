package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;

import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Account implements RealmModel {
    @PrimaryKey
    public String _id;
    public String username;
    public String password;
    public String updatedAt;
    public String createdAt;
    public User userId;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setUsername(String username){this.username = username;}

    public String getUsername(){return this.username;}

    public void setPassword(String password){}

    public String getUpdatedAt(){return  this.updatedAt;}

    public void setUpdatedAt(String updatedAt){this.updatedAt = updatedAt;}

    public String getCreatedAt(){return this.createdAt;}

    public void setCreatedAt(String createdAt){this.createdAt = createdAt;}

    public User getUserId(){return this.userId;}

    public void setUserId(User userId){this.userId = userId;}

}
