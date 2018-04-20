package org.schulcloud.mobile.data.model.responseBodies;

public class AccountResponse {
    public String displayName;
    public String _id;
    public String userId;

    public AccountResponse(String displayName, String _id, String userId) {
        this.displayName = displayName;
        this.userId = userId;
        this._id = _id;
    }
}
