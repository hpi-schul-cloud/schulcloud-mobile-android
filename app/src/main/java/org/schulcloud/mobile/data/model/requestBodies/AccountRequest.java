package org.schulcloud.mobile.data.model.requestBodies;


public class AccountRequest {
    public String username;
    public String password;
    public String _id;

    public AccountRequest(String username, String password, String _id){
        this.username = username;
        this.password = password;
        this._id = _id;
    }
}
