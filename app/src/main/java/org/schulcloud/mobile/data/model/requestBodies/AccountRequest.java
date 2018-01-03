package org.schulcloud.mobile.data.model.requestBodies;


public class AccountRequest {
    public String displayName;
    public String password;

    public AccountRequest(String displayName, String password){
        this.displayName = displayName;
        this.password = password;
    }
}
