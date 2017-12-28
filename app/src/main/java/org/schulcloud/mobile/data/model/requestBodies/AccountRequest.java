package org.schulcloud.mobile.data.model.requestBodies;

/**
 * Created by araknor on 28.12.17.
 */

public class AccountRequest {
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String schoolId;
    public String displayName;
    public String gender;

    public AccountRequest(String _id, String firstName, String lastName, String email, String schoolId,
                          String displayName, String gender) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.displayName = displayName;
        this.gender = gender;
    }
}
