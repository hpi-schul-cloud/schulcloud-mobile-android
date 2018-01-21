package org.schulcloud.mobile.data.model.requestBodies;

public class PasswordRecoveryRequest {
    String username;

    public PasswordRecoveryRequest(String username) {
        this.username = username;
    }
}
