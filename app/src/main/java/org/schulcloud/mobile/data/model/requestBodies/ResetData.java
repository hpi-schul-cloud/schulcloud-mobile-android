package org.schulcloud.mobile.data.model.requestBodies;

public class ResetData {
    String accountId;
    String password;

    public ResetData(String accountId,String password){
        this.accountId = accountId;
        this.password = password;
    }
}
