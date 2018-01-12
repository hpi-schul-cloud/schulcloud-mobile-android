package org.schulcloud.mobile.data.model.requestBodies;


public class AccountRequest {
    public String username;
    public String password;

    public AccountRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}
