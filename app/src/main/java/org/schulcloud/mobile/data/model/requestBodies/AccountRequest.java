package org.schulcloud.mobile.data.model.requestBodies;


public class AccountRequest {
    public String username;
    public String password;
    public String password_verification;
    public String _id;


    public AccountRequest(String username, String password, String _id, String password_verification){
        this.username = username;
        this.password = password;
        this.password_verification = password_verification;
        this._id = _id;
    }
}
