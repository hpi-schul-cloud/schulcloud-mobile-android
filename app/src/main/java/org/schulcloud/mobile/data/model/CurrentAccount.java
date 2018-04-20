package org.schulcloud.mobile.data.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;

public class CurrentAccount{
    public String userId;
    public String accountId;
    public String username;

    public String getUserId(){
        return userId;
    }

    public String getAccountId(){
        return accountId;
    }

    public String getUsername(){
        return username;
    }
}
