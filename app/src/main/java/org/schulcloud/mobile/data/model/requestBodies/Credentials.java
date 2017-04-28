package org.schulcloud.mobile.data.model.requestBodies;

public class Credentials {
    public String username;
    public String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}