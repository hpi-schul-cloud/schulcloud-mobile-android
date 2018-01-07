package org.schulcloud.mobile.data.model.responseBodies;

public class ProfileResponse {
    public String displayName;
    public String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String schoolId;
    public String gender;

    public ProfileResponse(UserResponse userResponse, AccountResponse accountResponse){
        this.displayName = accountResponse.displayName;
        this._id = userResponse._id;
        this.firstName = userResponse.firstName;
        this.lastName = userResponse.lastName;
        this.email = userResponse.email;
        this.schoolId = userResponse.schoolId;
        this.gender = userResponse.gender;
    }
}
